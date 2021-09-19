# Infrastructure


- Configure AWS CLI

- Create named profile using AWS CLI command ``aws configure --profile dev``

- Export dev profile by using command ``export AWS_PROFILE=dev``

- When creating AWS infrastructure in dev enironment using terraform go to below path``cd infrastructure/terraform/dev``

- If you want to create multiple VPC with same configuration edit below code in infrastructure/terraform/dev/main.tf

	``module "<vpc_logical_name>"{
    	source="../modules/vpc"
    	vpc_id=module.<vpc_logical_name>.vpc_id
	}
	 ``

- Initialize the working directory as terraform directory using ``terraform init``

- Create execution plan using command ``terraform plan``

- Apply terraform for creating Infrastructure as Code using command ``terraform apply``

- To destroy the Infrastructure just built use command ``terraform destroy``

Note: Before deleting the infrastructure make sure that all the EC2 instances created in VPC are terminated
as it may take more time for destroying all the resources.

