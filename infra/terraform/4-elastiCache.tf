##########################################
# 4-ElastiCache.tf : Redis ElastiCache 프리티어 구성 / private subnet
##########################################

resource "aws_security_group" "redis_sg" {
  name   = "${var.project_name}-${var.customer_id}-redis-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id] # App EC2에서만 접근 가능
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.customer_id}-redis-sg"
  }
}

resource "aws_elasticache_subnet_group" "redis_subnet" {
  name       = "${var.project_name}-${var.customer_id}-redis-subnet"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_c.id]
}

resource "aws_elasticache_parameter_group" "redis_62_param" {
  name        = "${var.project_name}-${var.customer_id}-redis6-2"
  family      = "redis6.x"
  description = "Custom parameter group for Redis 6.2"

  # (선택) 커스텀 파라미터 지정 예시
  # parameter {
  #   name  = "maxmemory-policy"
  #   value = "allkeys-lru"
  # }
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "${var.project_name}-${var.customer_id}-redis"
  engine               = "redis"
  engine_version       = "6.2"
  node_type            = "cache.t3.micro"
  num_cache_nodes      = 1
  parameter_group_name = aws_elasticache_parameter_group.redis_62_param.name
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.redis_subnet.name
  security_group_ids   = [aws_security_group.redis_sg.id]

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-redis"
    }
  )
}

