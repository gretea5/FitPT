##########################################
# 3-rds.tf : MySQL RDS 프리티어 구성 / private subnet
##########################################

resource "aws_db_subnet_group" "rds" {
  name       = "${var.project_name}-${var.customer_id}-rds-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_c.id]

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-rds-subnet-group"
    }
  )
}

resource "aws_security_group" "rds_sg" {
  name   = "${var.project_name}-${var.customer_id}-rds-sg"
  vpc_id = aws_vpc.main.id

  # 들어오는 것만 허용
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id]
  }

  # 나가는 db 모두 내부 통신 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.customer_id}-rds-sg"
  }
}

resource "aws_db_instance" "mysql" {
  identifier              = "${var.project_name}-${var.customer_id}-mysql"
  engine                  = "mysql"
  engine_version          = "8.0.42"
  instance_class          = "db.t3.micro" # 프리티어
  allocated_storage       = 20
  storage_type            = "gp2"
  username                = "admin"
  password                = var.db_password
  db_subnet_group_name    = aws_db_subnet_group.rds.name
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  skip_final_snapshot     = true
  publicly_accessible     = false
  backup_retention_period = 1
  deletion_protection     = false

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-mysql"
    }
  )
}
