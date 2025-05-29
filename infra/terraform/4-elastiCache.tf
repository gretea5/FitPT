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

# 비밀번호 설정 레디스와 없는 레디스 리소스 다름
resource "aws_elasticache_replication_group" "redis" {
  replication_group_id       = "${var.project_name}-${var.customer_id}-redis"
  description                = "Redis with AUTH & TLS"   # 변경된 인수명, 필수
  engine                     = "redis"
  engine_version             = "6.2"
  node_type                  = "cache.t3.micro"
  num_cache_clusters         = 1                        # 변경된 인수명
  automatic_failover_enabled = false

  auth_token                  = var.redis_auth_token
  transit_encryption_enabled  = true

  subnet_group_name           = aws_elasticache_subnet_group.redis_subnet.name
  security_group_ids          = [aws_security_group.redis_sg.id]

  tags = merge(
    local.common_tags,
    { Name = "${var.project_name}-${var.customer_id}-redis" }
  )
}

