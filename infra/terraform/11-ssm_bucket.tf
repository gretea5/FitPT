##################################
# 11-ssm_bucket.tf : Ansible-SSM 모듈 스테이징용 S3 버킷 및 정책
##################################


# IAM 사용자 생성: Ansible 제어 노드 인증용
resource "aws_iam_user" "ansible_controller" {
  name = "ansible-controller"
}

# 1- S3 버킷 생성 (전역 고유 이름 필요)
resource "aws_s3_bucket" "ansible_ssm" {
  bucket        = "${var.project_name}-${var.customer_id}-${var.ssm_bucket_name}"
  force_destroy = true
  tags = merge(
    local.common_tags,
    { Name = "${var.project_name}-ansible-ssm-bucket" }
  )
}

# 2- 퍼블릭 액세스 완전 차단
resource "aws_s3_bucket_public_access_block" "ansible_ssm_block" {
  bucket                  = aws_s3_bucket.ansible_ssm.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 3- 버킷 정책: 제어 노드에는 전체 권한, 인스턴스에는 읽기 권한만
data "aws_iam_policy_document" "ansible_ssm_bucket_policy" {
  statement {
    sid     = "ControllerListBucket"
    effect  = "Allow"
    actions = ["s3:ListBucket"]
    resources = [ aws_s3_bucket.ansible_ssm.arn ]
    principals {
      type        = "AWS"
      identifiers = [ aws_iam_user.ansible_controller.arn ]
    }
  }

  statement {
    sid     = "ControllerObjectAccess"
    effect  = "Allow"
    actions = [
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject",
    ]
    resources = [ "${aws_s3_bucket.ansible_ssm.arn}/*" ]
    principals {
      type        = "AWS"
      identifiers = [ aws_iam_user.ansible_controller.arn ]
    }
  }

  statement {
    sid     = "InstanceListBucket"
    effect  = "Allow"
    actions = ["s3:ListBucket"]
    resources = [ aws_s3_bucket.ansible_ssm.arn ]
    principals {
      type        = "AWS"
      identifiers = [ aws_iam_role.ec2_role.arn ]
    }
  }

  statement {
    sid     = "InstanceGetObject"
    effect  = "Allow"
    actions = ["s3:GetObject"]
    resources = [ "${aws_s3_bucket.ansible_ssm.arn}/*" ]
    principals {
      type        = "AWS"
      identifiers = [ aws_iam_role.ec2_role.arn ]
    }
  }
}

resource "aws_s3_bucket_policy" "ansible_ssm_policy" {
  bucket = aws_s3_bucket.ansible_ssm.id
  policy = data.aws_iam_policy_document.ansible_ssm_bucket_policy.json
}
