###########################################
# 5-iam.tf : EC2용 IAM Role 구성 (옵션)
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
      {
        Effect   = "Allow",
        Action   = [
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
      {
        "Effect": "Allow",
        "Action": [
          "rds:CreateDBSubnetGroup",
          "rds:DescribeDBSubnetGroups",
          "rds:DeleteDBSubnetGroup"
        ],
        "Resource": "*"
      }

    ]
  })
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.project_name}-${var.customer_id}-ec2-profile"
  role = aws_iam_role.ec2_role.name
}
