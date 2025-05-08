terraform {
  backend "s3" {
    bucket         = "fitpt-tf-state-bucket"
    key            = "global/s3/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
