# AMI Infrastructure

This repository is contains the terraform code for for granting policy to ghactions-ami for creating AMI.

Follow below commands for granting policy access to ghactions-ami I am user

``cd ami-infrastructure``
``terraform init``
``terraform plan -var-file=variables.tfvars``
``terraform apply -var-file=variables.tfvars``