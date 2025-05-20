##################################
# 1-vpc.tf : VPC 및 네트워크 구성
# Public subnet : Bastion Host EC2
# private subnet : Server EC2, RDS(MySQL), ElastiCache(Redis)
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

resource "aws_key_pair" "app_keypair" {
  key_name   = var.app_keypair_name
  public_key = local.app_keypair_public_key
}

# 퍼블릭 서브넷 (Bastion, NAT 용) - AZ a
resource "aws_subnet" "public_a" {
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

# 퍼블릭 서브넷 (Bastion, NAT 용) - AZ c
resource "aws_subnet" "public_c" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, 1)
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = true

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-public-subnet-c"
    }
  )
}

# 프라이빗 서브넷 (RDS, ElastiCache, Server APP용) - AZ a
resource "aws_subnet" "private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, 10)
  availability_zone = "ap-northeast-2a"

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.customer_id}-private-subnet-a"
    }
  )
}

# 프라이빗 서브넷 (RDS, ElastiCache,  Server App용) - AZ c
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

# NAT Gateway용 EIP
resource "aws_eip" "nat_eip" {
  domain = "vpc"
}

# NAT Gateway
resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_a.id
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

# Public Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
}

resource "aws_route_table_association" "public_a" {
  subnet_id      = aws_subnet.public_a.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_c" {
  subnet_id      = aws_subnet.public_c.id
  route_table_id = aws_route_table.public.id
}


# Private Route Table (via NAT)

resource "aws_route_table_association" "private_a" {
  subnet_id      = aws_subnet.private_a.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private_c" {
  subnet_id      = aws_subnet.private_c.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }
}