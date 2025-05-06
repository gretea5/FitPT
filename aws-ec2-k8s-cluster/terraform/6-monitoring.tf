resource "aws_instance" "monitoring" {
  ami                         = lookup(var.amis, var.region)
  instance_type               = "t2.micro"
  subnet_id                   = aws_subnet.kubernetes.id
  vpc_security_group_ids      = [aws_security_group.kubernetes.id]
  key_name                    = var.default_keypair_name
  associate_public_ip_address = true

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
    delete_on_termination = true
  }

  tags = {
    Name = "monitoring"
    Role = "PrometheusAgent"
    ansibleNodeType = "monitoring"
    "Owner" = "${var.owner}"
  }

}