output "bastion_public_ip" {
  description = "Bastion Host EIP (운영자 접속용)"
  value       = aws_eip.bastion.public_ip
}

output "app_instance_public_ips" {
  description = "App 서버 퍼블릭 IP 목록"
  value       = [for instance in aws_instance.app : instance.public_ip]
}

output "rds_endpoint" {
  description = "RDS MySQL Endpoint 주소"
  value       = aws_db_instance.mysql.endpoint
  sensitive   = true
}

output "rds_address" {
  description = "RDS MySQL 도메인 주소 (포트 제외)"
  value       = aws_db_instance.mysql.address
}

output "rds_port" {
  description = "RDS MySQL 포트 번호"
  value       = aws_db_instance.mysql.port
}
