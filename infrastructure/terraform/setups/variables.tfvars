region = "us-east-1"

vpc_cidr_tag = [
  { cidr = "10.0.0.0/16" }
]

subnet_details = [
  { cidr = "10.0.1.0/24", tag = "subnet-1", zone = "us-east-1a" },
  { cidr = "10.0.2.0/24", tag = "subnet-2", zone = "us-east-1b" },
  { cidr = "10.0.3.0/24", tag = "subnet-3", zone = "us-east-1c" }
]

route_details = { ipv4_cidr = "0.0.0.0/0", ipv6_cidr = "::/0" }