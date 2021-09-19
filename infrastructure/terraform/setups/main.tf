provider "aws" {
  region = var.region
}

variable "region" {
  description = "region"
  type        = string
}

variable "ami_created" {
  description = "ami created in dev and shared in prod"
  type        = string
}

variable "db_user" {
  description = "database username"
  type        = string
}

variable "db_password" {
  description = "database password"
  type        = string
}

variable "zone_id" {
  description = "zone id required for creating route53"
  type        = string
}

variable "name" {
  description = "name required for creating route53"
  type        = string
}

variable "aws_account_id" {
  description = "account id"
  type        = string
}

variable "certificate_id" {
  description = "certicate id for ssl"
  type        = string
}

variable "bucket_name" {
  description = "bucket name"
  type        = string
  default     = "webapp.deepika.prod"
}

variable "vpc_cidr_tag" {
  description = "cdr and vpc tagname"
  type        = list(object({ cidr = string }))
}

variable "subnet_details" {
  description = "cidr, tag name and AZ of subnet"
  type        = list(object({ cidr = string, tag = string, zone = string }))
}

variable "route_details" {
  description = "cidr, tag name for route"
  type        = object({ ipv4_cidr = string, ipv6_cidr = string })
}

variable "key_name" {
  description = "key name"
  type        = string
  default     = "cyse6225_spring2021"

}

variable "database" {
  description = "database name"
  type        = string
  default     = "csye6225"

}


locals {
  vpc_name              = "${terraform.workspace}-vpc"
  subnet_name           = terraform.workspace
  application_sg_name   = "${terraform.workspace}-application"
  database_sg_name      = "${terraform.workspace}-database"
  internet_gateway_name = "${terraform.workspace}-internet-gateway"
  route_table_name      = "${terraform.workspace}-route-table"
  loadbalancer_sg_name  = "${terraform.workspace}-loadbalancer"

}

#Creating VPC
resource "aws_vpc" "vpc" {
  cidr_block           = var.vpc_cidr_tag[0].cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = local.vpc_name
  }
}

# Creating subnet
resource "aws_subnet" "subnet-01" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = var.subnet_details[0].cidr
  availability_zone = var.subnet_details[0].zone
  tags = {
    Name = join("-", [local.subnet_name, var.subnet_details[0].tag])
  }
}

resource "aws_subnet" "subnet-02" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = var.subnet_details[1].cidr
  availability_zone = var.subnet_details[1].zone
  tags = {
    Name = join("-", [local.subnet_name, var.subnet_details[1].tag])
  }
}

resource "aws_subnet" "subnet-03" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = var.subnet_details[2].cidr
  availability_zone = var.subnet_details[2].zone
  tags = {
    Name = join("-", [local.subnet_name, var.subnet_details[2].tag])
  }
}

# Internet Gateway
resource "aws_internet_gateway" "internet-gateway" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    Name = local.internet_gateway_name
  }
}

# Route table
resource "aws_route_table" "first-route-table" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = var.route_details.ipv4_cidr
    gateway_id = aws_internet_gateway.internet-gateway.id
  }

  route {
    ipv6_cidr_block = var.route_details.ipv6_cidr
    gateway_id      = aws_internet_gateway.internet-gateway.id
  }

  tags = {
    Name = local.route_table_name
  }
}

resource "aws_route_table_association" "association1" {
  subnet_id      = aws_subnet.subnet-01.id
  route_table_id = aws_route_table.first-route-table.id
}

resource "aws_route_table_association" "association2" {
  subnet_id      = aws_subnet.subnet-02.id
  route_table_id = aws_route_table.first-route-table.id
}

resource "aws_route_table_association" "association3" {
  subnet_id      = aws_subnet.subnet-03.id
  route_table_id = aws_route_table.first-route-table.id
}

#Security Group
resource "aws_security_group" "application-sg" {
  name        = "application"
  description = "Security group for EC2 instance"
  vpc_id      = aws_vpc.vpc.id
  # ingress {
  #   description = "HTTPS"
  #   from_port   = 443
  #   to_port     = 443
  #   protocol    = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"]
  # }
  ingress {
    description = "Tomcat"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    security_groups = [
      aws_security_group.loadbalancer-sg.id
    ]
  }


  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # ingress {
  #   description = "Tomcat"
  #   from_port   = 8080
  #   to_port     = 8080
  #   protocol    = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"]
  # }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = local.application_sg_name
  }
}

# DB security group 
resource "aws_security_group" "database-sg" {
  name        = "database"
  description = "DB security group"
  vpc_id      = aws_vpc.vpc.id
  ingress {
    description     = "MySQL"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = ["${aws_security_group.application-sg.id}"]
  }
  tags = {
    Name = local.database_sg_name
  }
}

#loadbalacer scurity group
resource "aws_security_group" "loadbalancer-sg" {
  name        = "loadbalancer-sg"
  description = "Security group for loadbalancer"
  vpc_id      = aws_vpc.vpc.id

  # ingress {
  #   description = "HTTP"
  #   from_port   = 80
  #   to_port     = 80
  #   protocol    = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"]
  # }
  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    description = "Tomcat"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = local.loadbalancer_sg_name
  }
}


# S3 bucket
resource "aws_s3_bucket" "s3bucket" {
  bucket        = "webapp.deepika.${terraform.workspace}"
  acl           = "private"
  force_destroy = true
  // storage_class = "STANDARD"

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  # Required for force delete of bucket when not empty along with`force_destroy = true` 
  lifecycle {
    prevent_destroy = false
  }

  lifecycle_rule {
    id      = "downgrade"
    enabled = true
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }

  tags = {
    Name = "s3bucket"
  }
}


# Creating subnet group for sb instance
resource "aws_db_subnet_group" "database-subnet-group" {
  name       = "database-subnet-group"
  subnet_ids = ["${aws_subnet.subnet-01.id}", "${aws_subnet.subnet-02.id}", "${aws_subnet.subnet-03.id}"]
  tags = {
    Name = "database-subnet-group"
  }
}



// Creating RDS instance
resource "aws_db_instance" "rds-instance" {
  engine                  = "mysql"
  instance_class          = "db.t3.micro"
  multi_az                = false
  identifier              = "csye6225"
  name                    = "csye6225"
  username                = var.db_user
  password                = var.db_password
  publicly_accessible     = false
  vpc_security_group_ids  = ["${aws_security_group.database-sg.id}"]
  db_subnet_group_name    = aws_db_subnet_group.database-subnet-group.name
  allocated_storage       = 5
  skip_final_snapshot     = true
  apply_immediately       = true
  backup_retention_period = 0
  storage_encrypted       = true
  kms_key_id              = aws_kms_key.rds-kms-key.arn
}

output "rds_end_point" {
  value = aws_db_instance.rds-instance.endpoint
}


#creating IAM role CodeDeployEC2ServiceRole
resource "aws_iam_role" "CodeDeployEC2ServiceRole" {
  name = "CodeDeployEC2ServiceRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Sid    = ""
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })
}


data "aws_iam_policy_document" "kms-policy-document" {
  statement {
    actions = [
      "kms:*"
    ]
    resources = [
      "*"
    ]
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.aws_account_id}:user/${terraform.workspace}"]
    }
  }

  statement {
    actions = [
      "kms:*"
    ]
    resources = [
      "*"
    ]
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.aws_account_id}:root"]
    }
  }

  statement {
    actions = [
      "kms:Encrypt",
      "kms:Decrypt",
      "kms:ReEncrypt*",
      "kms:GenerateDataKey*",
      "kms:DescribeKey",
      "kms:CreateGrant",
      "kms:ListGrants",
      "kms:RevokeGrant"
    ]
    resources = [
      "*"
    ]
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.aws_account_id}:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling"]
    }
  }
}

data "aws_iam_policy_document" "gh-code-policy-document" {
  statement {

    actions = [
      "codedeploy:RegisterApplicationRevision",
      "codedeploy:GetApplicationRevision"
    ]
    resources = [
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:application:csye6225-webapp",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:application:csye6225-serverless"
    ]
  }

  statement {
    actions = [
      "codedeploy:CreateDeployment",
      "codedeploy:GetDeployment"
    ]
    resources = [
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentgroup:csye6225-webapp/csye6225-webapp-deployment",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentgroup:csye6225-serverless/csye6225-serverless-deployment"
    ]
  }

  statement {

    actions = [
      "codedeploy:GetDeploymentConfig"
    ]

    resources = [
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.OneAtATime",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.HalfAtATime",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.AllAtOnce",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.AllAtOnce",
      "arn:aws:codedeploy:us-east-1:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.LambdaAllAtOnce"
    ]
  }

  statement {
    actions = [
      "lambda:GetAlias",
      "lambda:UpdateFunctionCode"
    ]
    resources = [aws_lambda_function.email-sender-function.arn]
  }
}

#Creating policy 1 for IAM user ghactionIAM user used for deployment in production
resource "aws_iam_policy" "gh-code-deploy" {
  name        = "gh-code-deploy"
  description = "Policy granting IAM user ghaction for getting and registering application revision"
  policy      = data.aws_iam_policy_document.gh-code-policy-document.json
}

#Creating policy 2 for IAM user  ghaction IAM user used for deployment in production
resource "aws_iam_policy" "gh-upload-to-s3" {
  name        = "gh-upload-to-s3"
  description = "Policy for IAM user ghaction for having access for getting , putting and listing s3 bucket objects"
  policy      = file("GH-Upload-To-s3.json")
}


#Attaching policy1 to ghactions IAM  user profile
resource "aws_iam_user_policy_attachment" "gh-policy1-attach" {
  user       = "ghactions"
  policy_arn = aws_iam_policy.gh-code-deploy.arn
}

#Attaching policy2 to ghaction IAM user profile
resource "aws_iam_user_policy_attachment" "gh-policy2-attach" {
  user       = "ghactions"
  policy_arn = aws_iam_policy.gh-upload-to-s3.arn
}


# Creating IAM Policy
resource "aws_iam_policy" "codedeploy-ec2-s3" {
  name        = "codedeploy-ec2-s3"
  description = "Policy to attached to CodeDeployEC2ServiceRole IAM  role "
  policy      = file("CodeDeploy-EC2-S3.json")
}


data "aws_iam_policy_document" "sns-policy-document" {
  statement {
    actions = [
      "sns:Publish"
    ]
    resources = [
      aws_sns_topic.book-event.arn
    ]
  }
}

resource "aws_iam_policy" "aws-sns-policy" {
  name        = "iam-log-sns-policy"
  description = "Policy granting permission to publish message to sns"
  policy      = data.aws_iam_policy_document.sns-policy-document.json
}

#Attaching policy aws-sns-policy to CodeDeployEC2ServiceRole so that CodeDeployEC2ServiceRole 
#have permission to publish message to sns
resource "aws_iam_role_policy_attachment" "codedeploy-ec2-sns-attach" {
  role       = aws_iam_role.CodeDeployEC2ServiceRole.name
  policy_arn = aws_iam_policy.aws-sns-policy.arn
}

resource "aws_iam_role_policy_attachment" "codedeploy-ec2-s3-attach" {
  role       = aws_iam_role.CodeDeployEC2ServiceRole.name
  policy_arn = aws_iam_policy.codedeploy-ec2-s3.arn
}


#creating IAM role CodeDeployServiceRole for EC2 instances that will be used to host your web application
# create a service role for codedeploy
resource "aws_iam_role" "CodeDeployServiceRole" {
  name = "CodeDeployServiceRole"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "codedeploy.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}


# Attach AWS managed policy called AWSCodeDeployRole
# required for deployments which are used by an EC2 compute platform
resource "aws_iam_role_policy_attachment" "codedeploy_service" {
  role       = aws_iam_role.CodeDeployServiceRole.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
}

resource "aws_iam_role_policy_attachment" "codedeploy_lambda_service" {
  role       = aws_iam_role.CodeDeployServiceRole.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRoleForLambda"
}

resource "aws_iam_role_policy_attachment" "codedeploy_s3_service" {
  role       = aws_iam_role.CodeDeployServiceRole.name
  policy_arn = aws_iam_policy.codedeploy-ec2-s3.arn ## providing the access to code deploy bucket, the same as ec2 code deploy bucket
}

resource "aws_codedeploy_app" "csye6225-webapp" {
  name             = "csye6225-webapp"
  compute_platform = "Server"
}


resource "aws_codedeploy_deployment_group" "csye6225-webapp-deployment" {
  app_name              = aws_codedeploy_app.csye6225-webapp.name
  deployment_group_name = "csye6225-webapp-deployment"
  service_role_arn      = aws_iam_role.CodeDeployServiceRole.arn

  deployment_config_name = "CodeDeployDefault.OneAtATime"
  autoscaling_groups     = ["${aws_autoscaling_group.autoscaling-group.name}"]
  load_balancer_info {
    target_group_info {
      name = aws_lb_target_group.target-group.name
    }
  }
  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "IN_PLACE"
  }

  auto_rollback_configuration {
    enabled = false
  }
}


resource "aws_codedeploy_app" "csye6225-serverless" {
  name             = "csye6225-serverless"
  compute_platform = "Lambda"
}

resource "aws_codedeploy_deployment_group" "csye6225-serverless-deployment" {
  app_name               = aws_codedeploy_app.csye6225-serverless.name
  deployment_group_name  = "csye6225-serverless-deployment"
  service_role_arn       = aws_iam_role.CodeDeployServiceRole.arn
  deployment_config_name = "CodeDeployDefault.LambdaAllAtOnce"
  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }
}

resource "aws_route53_record" "route53" {
  zone_id = var.zone_id
  name    = var.name
  type    = "A"
  alias {
    name                   = aws_lb.webapp-load-balancer.dns_name
    zone_id                = aws_lb.webapp-load-balancer.zone_id
    evaluate_target_health = true
  }
}

#Attaching policy "CloudWatchAgentServerPolicy" for CloudWatchAgentServerRole role
resource "aws_iam_role_policy_attachment" "CloudWatchAgentServerPolicy" {
  role       = aws_iam_role.CodeDeployEC2ServiceRole.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"

}

#Note: For accessing s3 bucket and codedeploy bucket we have created a iam role CodeDeployEC2ServiceRole which in turns attach to EC2
#For assignment 6 we are attaching 3rd  policy "CloudWatchAgentServerPolicy" to CodeDeployEC2ServiceRole which inturns attach with EC2 instance
resource "aws_iam_instance_profile" "ec2-profile" {
  name = "CloudWatchAgentEC2-profile"
  role = aws_iam_role.CodeDeployEC2ServiceRole.name
}

###########Assignment 7####################
##Note used for assignment 9
//launch configuration for load balancer
# resource "aws_launch_configuration" "launch-config" {
#   name                        = "asg_launch_config"
#   image_id                    = var.ami_created
#   instance_type               = "t2.micro"
#   key_name                    = var.key_name
#   user_data                   = <<-EOF
#               #!/bin/bash
#               sudo apt-get update
#               sudo su              
#               sudo echo "export DB_PORT=3306" >> /etc/profile.d/envvars.sh
#               sudo echo "export DB_DATABASE=${var.database}" >> /etc/profile.d/envvars.sh
#               sudo echo "export TOPIC_ARN=${aws_sns_topic.book-event.arn}" >> /etc/profile.d/envvars.sh
#               sudo echo "export SPRING_DATASOURCE_USERNAME=${var.db_user}" >> /etc/profile.d/envvars.sh
#               sudo echo "export SPRING_DATASOURCE_PASSWORD=${var.db_password}" >> /etc/profile.d/envvars.sh
#               sudo echo "export IMAGES_BUCKET_NAME=${var.bucket_name}" >> /etc/profile.d/envvars.sh
#               EOF                               
#   iam_instance_profile        = aws_iam_instance_profile.ec2-profile.name
#   associate_public_ip_address = true
#   security_groups             = ["${aws_security_group.application-sg.id}"]
#   # root_block_device {
#   #   encrypted = true
#   # }
#   # ebs_block_device {
#   #   encrypted = true
#   # }
#   lifecycle {
#     create_before_destroy = true
#   }
# }

data "template_file" "output" {
  template = <<-EOF
              #!/bin/bash
              sudo apt-get update
              sudo su              
              sudo echo "export DB_PORT=3306" >> /etc/profile.d/envvars.sh
              sudo echo "export DB_HOSTNAME=${aws_db_instance.rds-instance.endpoint}" >> /etc/profile.d/envvars.sh
              sudo echo "export DB_DATABASE=${var.database}" >> /etc/profile.d/envvars.sh
              sudo echo "export TOPIC_ARN=${aws_sns_topic.book-event.arn}" >> /etc/profile.d/envvars.sh
              sudo echo "export SPRING_DATASOURCE_USERNAME=${var.db_user}" >> /etc/profile.d/envvars.sh
              sudo echo "export SPRING_DATASOURCE_PASSWORD=${var.db_password}" >> /etc/profile.d/envvars.sh
              sudo echo "export IMAGES_BUCKET_NAME=${var.bucket_name}" >> /etc/profile.d/envvars.sh
              EOF     
}

resource "aws_launch_template" "launch-template" {
  name = "asg_launch_template"
  block_device_mappings {
    device_name = "/dev/sda1"
    ebs {
      volume_size = 8
      kms_key_id  = aws_kms_key.ebs-kms-key.arn
      encrypted   = "true"
    }
  }
  image_id      = var.ami_created
  instance_type = "t2.micro"
  key_name      = var.key_name
  iam_instance_profile {
    name = aws_iam_instance_profile.ec2-profile.name
  }
  network_interfaces {
    associate_public_ip_address = true
    security_groups             = ["${aws_security_group.application-sg.id}"]
  }
  user_data = base64encode(data.template_file.output.template)
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_autoscaling_group" "autoscaling-group" {
  name = "autoscaling-group"
  #launch_configuration = aws_launch_configuration.launch-config.name #not used for assignment 9
  launch_template {
    id      = aws_launch_template.launch-template.id
    version = "$Latest"
  }
  min_size                  = 1 #decreasing to 1 as not required for assignment 9
  max_size                  = 1 #decreasing to 1 as not required for assignment 9
  target_group_arns         = ["${aws_lb_target_group.target-group.arn}"]
  health_check_grace_period = 300
  default_cooldown          = 60
  desired_capacity          = 1 #decreasing to 1 as not required for assignment 9
  vpc_zone_identifier       = [aws_subnet.subnet-01.id, aws_subnet.subnet-02.id, aws_subnet.subnet-03.id]
  tags = [
    {
      key                 = "name"
      value               = "ci/cd"
      propagate_at_launch = true
    }
  ]
}

#Load balancer
resource "aws_lb" "webapp-load-balancer" {
  name                       = "webapp-load-balancer"
  internal                   = false #used for internet facing
  ip_address_type            = "ipv4"
  load_balancer_type         = "application"
  security_groups            = ["${aws_security_group.loadbalancer-sg.id}"]
  subnets                    = [aws_subnet.subnet-01.id, aws_subnet.subnet-02.id, aws_subnet.subnet-03.id]
  enable_deletion_protection = false
}

#target group for load balancer
resource "aws_lb_target_group" "target-group" {
  name        = "aws-target-group-load-balancer"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.vpc.id
  target_type = "instance"

  health_check {
    port                = 8080
    matcher             = 200
    interval            = 10
    path                = "/"
    timeout             = 5
    healthy_threshold   = 5
    unhealthy_threshold = 2
  }
}

#aws load balancer listner
resource "aws_lb_listener" "lb-listner" {
  load_balancer_arn = aws_lb.webapp-load-balancer.arn
  # port              = "80"
  # protocol          = "HTTP"
  port            = "443" # updated to listen at 443 port assignment -9
  protocol        = "HTTPS"
  certificate_arn = "arn:aws:acm:us-east-1:${var.aws_account_id}:certificate/${var.certificate_id}"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.target-group.arn
  }
}

#Policy for scaling up the  EC2 instances
resource "aws_autoscaling_policy" "scale-up-policy" {
  name                   = "agents-scale-up"
  scaling_adjustment     = 1
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 60
  autoscaling_group_name = aws_autoscaling_group.autoscaling-group.name
}

resource "aws_cloudwatch_metric_alarm" "cloudwatch-cpu-high-alarm" {
  alarm_name          = "cloudwatch-cpu-high-alarm"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "5"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "300"
  statistic           = "Average"
  threshold           = "5"

  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.autoscaling-group.name
  }

  alarm_description = "This metric monitors ec2 cpu utilization"
  alarm_actions     = [aws_autoscaling_policy.scale-up-policy.arn]
}

#Policy for scaling down the  EC2 instances
resource "aws_autoscaling_policy" "scale-down-policy" {
  name                   = "agents-scale-down"
  scaling_adjustment     = -1
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 60
  autoscaling_group_name = aws_autoscaling_group.autoscaling-group.name
}

#Cloud watch metric alarm for cpu utilisation
resource "aws_cloudwatch_metric_alarm" "cloudwatch-cpu-low-down" {
  alarm_name          = "cloudwatch-cpu-low-alarm"
  comparison_operator = "LessThanOrEqualToThreshold"
  evaluation_periods  = "5"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "300"
  statistic           = "Average"
  threshold           = "3"

  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.autoscaling-group.name
  }

  alarm_description = "This metric monitors ec2 cpu utilization"
  alarm_actions     = [aws_autoscaling_policy.scale-down-policy.arn]
}

############# Assignment 8##########
##Creating SNS topic for book even CREATE/DELETE
resource "aws_sns_topic" "book-event" {
  name = "book-event-sns-topic"
}

##Providing permission to SNS "sns.amazonaws.com" to trigger lamda 
resource "aws_lambda_permission" "sns_permission" {
  statement_id  = "AllowExecutionFromSNS"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.email-sender-function.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = aws_sns_topic.book-event.arn
}

##Created subscription.Here lamda is subscribing for SNS topic so that once any message get published
#to sns lambda can get it
resource "aws_sns_topic_subscription" "lambda" {
  topic_arn = aws_sns_topic.book-event.arn
  protocol  = "lambda"
  endpoint  = aws_lambda_function.email-sender-function.arn
}


## Defining default lamda function which is lamdacode.zip. Zipping function in file and
## sendingis easy compared to puting file in s3 so zipped the file. 
## EmailSenderLamda is name of lamda function
## handler.sendEmail is handler defined in lambdacode.zip
resource "aws_lambda_function" "email-sender-function" {
  filename      = "lambdacode.zip"
  function_name = "EmailSenderLambda"
  role          = aws_iam_role.iam_for_lambda.arn
  handler       = "handler.sendEmail"
  runtime       = "nodejs14.x"
  publish       = true
  environment {
    variables = {
      user   = "${terraform.workspace}"
      domain = "${var.name}"
    }
  }
}

resource "aws_lambda_alias" "todos_create_publish_development" {
  function_name    = aws_lambda_function.email-sender-function.arn
  function_version = aws_lambda_function.email-sender-function.version
  lifecycle {
    ignore_changes = [function_version]
  }
  name = "development"
}

#IAM role created for lamda
resource "aws_iam_role" "iam_for_lambda" {
  name = "iam_for_lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

## IAM policy attachment document providing lamda access to getItem, Query,UpdateItem in DynamoDB 
##and creating logs in cloudwatch and sending email using SES
##
data "aws_iam_policy_document" "lambda-policy-document" {
  statement {

    actions = [
      "dynamodb:BatchGetItem",
      "dynamodb:GetItem",
      "dynamodb:Query",
      "dynamodb:Scan",
      "dynamodb:BatchWriteItem",
      "dynamodb:PutItem",
      "dynamodb:UpdateItem"
    ]
    resources = [
      "arn:aws:dynamodb:us-east-1:${var.aws_account_id}:table/email-tracking"
    ]
  }

  statement {
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = [
      "arn:aws:logs:*:*:*",
    ]
  }
  statement {
    actions = [
      "ses:SendEmail"
    ]
    resources = [
      "arn:aws:ses:us-east-1:${var.aws_account_id}:identity/${var.name}",
    ]
  }
}

#Policy for granting Lamda permission to log in cloudwatch and calling DynamoDB
resource "aws_iam_policy" "iam-lambda-policy" {
  name        = "iam-lambda-policy"
  description = "Policy granting LAMBDA permission to log and call dynamo"
  policy      = data.aws_iam_policy_document.lambda-policy-document.json
}

#Attaching  policy to IAM role am_for_lambda used for executing all the thing for lamda
resource "aws_iam_role_policy_attachment" "dynamo_lambda_policy_attachment" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.iam-lambda-policy.arn
}


#Creating Dynamo Table with table name "email-tracking" and hashkey .
#In DynamoDB primary key is composed with hashkey and sort key
resource "aws_dynamodb_table" "dynamo-email-tracking" {
  name         = "email-tracking"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "Id"
  attribute {
    name = "Id"
    type = "S"
  }
}


####Assignment 9#####


#KMS -key for ebs volume
resource "aws_kms_key" "ebs-kms-key" {
  description             = "ebs kms key"
  deletion_window_in_days = 7
  policy                  = data.aws_iam_policy_document.kms-policy-document.json
}

## KMS key for rds
resource "aws_kms_key" "rds-kms-key" {
  description             = "rds kms key"
  deletion_window_in_days = 7
}

