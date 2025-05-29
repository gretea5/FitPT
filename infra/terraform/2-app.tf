########################################
# 2-app.tf : Server EC2 / Docker 기반 App 노드 구성 / private subnet
########################################

resource "aws_security_group" "app_sg" {
  name   = "${var.project_name}-${var.customer_id}-app-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  # Prometheus Node Exporter port open
  ingress {
    from_port   = 9100
    to_port     = 9100
    protocol    = "tcp"
    self        = true                # 같은 SG 내 인스턴스 간 허용
    description = "Allow Prometheus scrape from App nodes"
  }

  # Outbound: SaaS API 호출 (443 외부 허용, NAT 통해)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "${var.project_name}-${var.customer_id}-app-sg"
  }
}

resource "aws_instance" "app" {
  count                       = var.number_of_app_nodes
  ami                         = lookup(var.amis, var.region)
  instance_type               = var.app_instance_type
  subnet_id                   = aws_subnet.private_a.id
  key_name                    = var.app_keypair_name
  associate_public_ip_address = false
  vpc_security_group_ids      = [aws_security_group.app_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.ec2_profile.name

  root_block_device {
    volume_size           = 50
    volume_type           = "gp3"
    delete_on_termination = true
  }

  tags = merge(
    local.common_tags,
    {
      Name            = "${var.project_name}-${var.customer_id}-app-${count.index}"
      Role            = "app"
      ansibleNodeType = "app"
      Owner             = "kkt3289"          # (필터에 쓰는 경우)
      Environment       = "dev"
    }
  )
}
