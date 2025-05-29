########################################
# variable.tf : AWS terrform 파일 변수 저장
########################################

locals {
  vpc_name      = "${var.project_name}-${var.customer_id}-vpc"
  ansibleFilter = "${var.project_name}-${var.customer_id}"
  common_tags = {
    Project     = var.project_name
    Customer    = var.customer_id
    Environment = var.environment
    Owner       = var.owner
  }
}

#############################
# Adjustable variables
#############################

variable "project_name" {
  description = "SaaS 서비스 또는 시스템 명"
  default     = "fitpt"
}

variable "customer_id" {
  description = "고객 고유 ID (지점/프랜차이즈용)"
  default     = "gym001"
}

variable "environment" {
  description = "배포 환경 (dev, staging, prod)"
  default     = "dev"
}

variable "number_of_app_nodes" {
  default = 1
}

variable "number_of_bastion_nodes" {
  default = 1
}

variable "number_of_monitor_nodes" {
  default = 1
}

# aws_security_ufw_setup
variable "allowed_ports" {
  description = "List of ports to open in security group"
  type        = list(number)
  default     = [22, 443, 3306, 6443, 2379, 2380, 10250]
}


# AWS_Rds_db_password_setup
variable "db_password" {
  description = "RDS root password"
  sensitive   = true
}

# AWS_Elasticache_Redis_password_setup
variable "redis_auth_token" {
  description = "ElastiCache Redis AUTH 토큰 (16~128자, 공백·'/', '\"', '@', '%' 제외)"
  type        = string
  sensitive   = true
}

# Ansible-SSM 모듈 스테이징용 S3 버킷 이름
variable "ssm_bucket_name" {
  description = "SSM용 Ansible 모듈 스테이징 S3 버킷 이름"
  type        = string
  default     = "ansible-ssm-bucket"
}


##########################
# Default variables (you can change for customizing)
##########################

# SaaS 용 관리자 IP 등록 부분
# SaaS 고객사 설치 시 아래 값은 각 고객의 운영자/지점 IP로 설정
# SaaS 사용자 IP를 등록하여 ssh 또는 접근 제한 가능
variable "control_cidr" {
  description = "CIDR for maintenance (SSH or admin access control)."
  default     = "0.0.0.0/0" # ← 지금은 모든 IP 허용 (개발용)
}

locals {
  default_keypair_public_key = file("../keys/tf-fitpt.pub")
}

locals {
  app_keypair_public_key = file("../keys/tf-fitpt-private.pub")
}

/*
## It triggers interpolation. It is recommended to use another way.
## TODO : Replace default_keypair_public_key as output?
variable default_keypair_public_key {
  description = "Public Key of the default keypair"
  default = "${file("../keys/tf-fitpt.pub")}"
}
*/

variable "default_keypair_name" {
  description = "Name of the KeyPair used for bastion nodes"
  default     = "tf-fitpt"
}

variable "app_keypair_name" {
  description = "Name of the KeyPair used for app"
  default     = "tf-fitpt-private"
}

variable "vpc_name" {
  default = ""
}

variable "ansibleFilter" {
  default = ""
}

variable "owner" {
  default = "kkt3289"
}

# Networking setup
variable "region" {
  default = "ap-northeast-2"
}

variable "zone" {
  default = "ap-northeast-2a"
}

### VARIABLES BELOW MUST NOT BE CHANGED ###

variable "vpc_cidr" {
  default = "10.43.0.0/16"
}

# Instances Setup
variable "amis" {
  description = "Default AMIs to use for nodes depending on the region"
  type        = map(string)
  default = {
    # Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
    ap-northeast-2 = "ami-05a7f3469a7653972"
    ap-northeast-1 = "ami-0c1907b6d738188e5"
    ap-southeast-1 = "ami-0a2e29e3b4fc39212"
    eu-central-1   = "ami-04a5bacc58328233d"
    eu-west-1      = "ami-0f0c3baa60262d5b9"
    sa-east-1      = "ami-0975628249d7e8952"
    us-east-1      = "ami-0f9de6e2d2f067fca"
    us-west-1      = "ami-05e1c8b4e753b29d3"
    us-west-2      = "ami-03f8acd418785369b"
  }
}

variable "default_instance_user" {
  default = "ubuntu"
}

variable "app_instance_type" {
  default = "t3.large"
}

variable "monitor_instance_type" {
  default = "t3.small"
}

variable "bastion_instance_type" {
  default = "t2.micro"
}