##################################
# Elastic Container Registry 등록 : jenkins 파이프라인 통한 jar 이미지 등록
##################################

locals {
  ecr_names = ["fitpt-app", "fitpt-admin"]
}

resource "aws_ecr_repository" "services" {
  for_each             = toset(local.ecr_names)
  name                 = each.value
  image_tag_mutability = "MUTABLE"
  tags                 = local.common_tags
}
