########################################
# 2-app.tf : Docker 기반 App 노드 구성
########################################

resource "aws_security_group" "app_sg" {
  name   = "${var.project_name}-${var.customer_id}-app-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.control_cidr]  # 운영자 SSH 접속(Bastion용)
  }

  # Outbound: SaaS API 호출 (443 외부 허용, NAT 통해)
  egress {
  from_port   = 443
  to_port     = 443
  protocol    = "tcp"
  cidr_blocks = ["203.0.113.0/24"]  # SaaS API Gateway CIDR
}

  tags = {
    Name = "${var.project_name}-${var.customer_id}-app-sg"
  }
}

resource "aws_instance" "app" {
  count         = var.number_of_app_nodes
  ami           = lookup(var.amis, var.region)
  instance_type = var.app_instance_type
  subnet_id     = aws_subnet.private_a.id
  key_name      = var.app_keypair_name
  associate_public_ip_address = false
  vpc_security_group_ids      = [aws_security_group.app_sg.id]
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name

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
    }
  )
}
