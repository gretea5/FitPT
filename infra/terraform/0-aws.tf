##################################
# 0-aws.tf : aws region 설정
##################################

provider "aws" {
  region = var.region
}