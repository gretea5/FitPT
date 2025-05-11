##################################
# 1-vpc.tf : VPC 및 네트워크 구성
##################################

resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true

  tags = merge(
    local.common_tags,
    {
      Name = var.vpc_name
    }
  )
}

resource "aws_vpc_dhcp_options" "default" {
  domain_name         = "${var.region}.compute.internal"
  domain_name_servers = ["AmazonProvidedDNS"]

  tags = local.common_tags
}

resource "aws_vpc_dhcp_options_association" "default" {
  vpc_id          = aws_vpc.main.id
  dhcp_options_id = aws_vpc_dhcp_options.default.id
}

##########
# Keypair
##########

resource "aws_key_pair" "default_keypair" {
  key_name   = var.default_keypair_name
  public_key = local.default_keypair_public_key
}

# 퍼블릭 서브넷 (App, Bastion용)
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, 0)
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-public-subnet-a"
    }
  )
}

# 프라이빗 서브넷 (RDS용) - AZ a
resource "aws_subnet" "private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, 10)
  availability_zone = "ap-northeast-2a"

  tags = merge(
    l
    ocal.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-private-subnet-a"
    }
  )
}

# 프라이빗 서브넷 (RDS용) - AZ c
resource "aws_subnet" "private_c" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, 11)
  availability_zone = "ap-northeast-2c"

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-private-subnet-c"
    }
  )
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-igw"
    }
  )
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = local.common_tags
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}
