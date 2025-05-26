###########################################
# 6-monitoring.tf : Monitoring EC2 / Docker 기반 Monitoring 노드 구성 / private subnet
###########################################

resource "aws_security_group" "monitor_sg" {
  name   = "${var.project_name}-${var.customer_id}-monitor-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  ingress {
    from_port       = 3000
    to_port         = 3000
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  ingress {
    from_port       = 9090
    to_port         = 9090
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.customer_id}-monitor-sg"
  }
}

resource "aws_instance" "monitor" {
  ami                         = lookup(var.amis, var.region)
  instance_type               = var.monitor_instance_type
  subnet_id                   = aws_subnet.private_a.id
  key_name                    = var.app_keypair_name
  associate_public_ip_address = false
  vpc_security_group_ids      = [aws_security_group.monitor_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.ec2_profile.name

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  tags = merge(
    local.common_tags,
    {
      Name              = "${var.project_name}-${var.customer_id}-monitor"
      Role              = "monitor"
      ansibleNodeType   = "monitor"
      Owner             = "kkt3289"
      Environment       = "dev"
    }
  )
}
