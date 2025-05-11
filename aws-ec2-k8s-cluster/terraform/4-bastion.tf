###########################################
# 4-bastion.tf : Bastion Host 구성
###########################################

resource "aws_security_group" "bastion_sg" {
  name   = "${var.project_name}-${var.customer_id}-bastion-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.control_cidr]  # 운영자 IP만 허용
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.customer_id}-bastion-sg"
  }
}

resource "aws_instance" "bastion" {
  ami                         = lookup(var.amis, var.region)
  instance_type               = var.bastion_instance_type
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.bastion_sg.id]
  key_name                    = var.default_keypair_name
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
  

  root_block_device {
    volume_size           = 30
    volume_type           = "gp3"
    delete_on_termination = true
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-bastion"
      Role = "bastion"
    }
  )

}

# Bastion EC2 고정 IP 지정
resource "aws_eip" "bastion" {
  instance = aws_instance.bastion.id
  domain      = "vpc"

  tags = merge(local.common_tags, {
    Name = "${var.project_name}-${var.customer_id}-bastion-eip"
  })
}
