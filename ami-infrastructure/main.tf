
# Variables and locals
variable "provider_region" {
  description = "region of the provider"
  type        = string
}


# IAM user and policy
provider "aws" {
  region = var.provider_region
}

# gh-ec2-ami policy 
resource "aws_iam_policy" "gh-ec2-ami" {
  name        = "gh-ec2-ami"
  description = "Policy for GitHub Actions"
  policy      = file("GH-EC2-AMI.json")
}

# Attach above policy to IAM user
resource "aws_iam_user_policy_attachment" "ami-attach-policy" {
  user       = "ghactions-ami"
  policy_arn = aws_iam_policy.gh-ec2-ami.arn
}
