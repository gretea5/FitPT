###########################################
# 7-iam.tf : EC2용 IAM Role 구성 (옵션)
###########################################

resource "aws_iam_role" "ec2_role" {
  name = "${var.project_name}-${var.customer_id}-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = local.common_tags
}

resource "aws_iam_role_policy" "ec2_policy" {
  name = "${var.project_name}-${var.customer_id}-ec2-policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      { # [1] EC2와 SSM 세션 매니저 관련 권한
        Effect = "Allow",
        Action = [
          "ssm:DescribeInstanceInformation",
          "ssm:GetCommandInvocation",
          "ssm:SendCommand",
          "ssm:ListCommands",
          "ssm:ListCommandInvocations",
          "ssm:StartSession",
          "ssm:TerminateSession",
          "ssm:DescribeSessions",
          "ec2:DescribeInstances",
          "ec2:DescribeTags"
        ],
        Resource = "*"
      },
      { # [2] RDS SubnetGroup 조작용 권한
        "Effect" : "Allow",
        "Action" : [
          "rds:CreateDBSubnetGroup",
          "rds:DescribeDBSubnetGroups",
          "rds:DeleteDBSubnetGroup"
        ],
        "Resource" : "*"
      },
      { # [3] ECR 이미지 가져오기 (Docker Pull 등)
        "Effect" : "Allow",
        "Action" : [
          "ecr:GetAuthorizationToken",
          "ecr:BatchGetImage",
          "ecr:GetDownloadUrlForLayer"
        ],
        "Resource" : "*"
      },
      { # [4] ACM 인증서 관련 (EC2가 직접 인증서 작업할 경우)
        "Effect" : "Allow",
        "Action" : [
          "acm:RequestCertificate",
          "acm:DescribeCertificate",
          "acm:DeleteCertificate",
          "acm:AddTagsToCertificate",
          "acm:ListCertificates",
          "acm:ListCertificates"
        ],
        "Resource" : "*"
      },
      { # [5] Route53 DNS 레코드 등록
        "Effect" : "Allow",
        "Action" : [
          "route53:GetHostedZone",
          "route53:ChangeResourceRecordSets",
          "route53:ListHostedZones",
          "route53:ListResourceRecordSets"
        ],
        "Resource" : "*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.project_name}-${var.customer_id}-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

resource "aws_iam_role_policy_attachment" "ssm_core_attach" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}