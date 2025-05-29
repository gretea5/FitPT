########################################
# 9-route53.tf : acm 인증서 및 DNS 설정
########################################

# 0- Hosted Zone 자동 조회
data "aws_route53_zone" "fitpt_store" {
  name         = "fitpt.store."   # 반드시 도메인 끝에 점(.)을 붙입니다
  private_zone = false
}

# 1- ACM 인증서 요청
resource "aws_acm_certificate" "alb_cert" {
  domain_name               = "app.fitpt.store"
  subject_alternative_names = ["admin.fitpt.store"]
  validation_method         = "DNS"
  tags = {
    Name = "fitpt-alb-cert"
  }
}

# 2- DNS 검증 레코드 생성
resource "aws_route53_record" "alb_cert_validation" {
  for_each = {
    for dvo in aws_acm_certificate.alb_cert.domain_validation_options : dvo.domain_name => {
      name  = dvo.resource_record_name
      type  = dvo.resource_record_type
      value = dvo.resource_record_value
    }
  }

  zone_id = data.aws_route53_zone.fitpt_store.zone_id
  name    = each.value.name
  type    = each.value.type
  records = [each.value.value]
  ttl     = 300
}

# 3- ACM 인증서 검증 완료 대기
resource "aws_acm_certificate_validation" "alb_cert_validation" {
  certificate_arn         = aws_acm_certificate.alb_cert.arn
  validation_record_fqdns = [
    for record in aws_route53_record.alb_cert_validation : record.fqdn
  ]
}

# 4- 애플리케이션용 A 레코드 (ALB 연결)
resource "aws_route53_record" "app_record" {
  zone_id = data.aws_route53_zone.fitpt_store.zone_id
  name    = "app.fitpt.store"
  type    = "A"

  alias {
    name                   = aws_lb.app_alb.dns_name
    zone_id                = aws_lb.app_alb.zone_id
    evaluate_target_health = true
  }
}

# 5- 관리자용 A 레코드 (ALB 연결)
resource "aws_route53_record" "admin_record" {
  zone_id = data.aws_route53_zone.fitpt_store.zone_id
  name    = "admin.fitpt.store"
  type    = "A"

  alias {
    name                   = aws_lb.app_alb.dns_name
    zone_id                = aws_lb.app_alb.zone_id
    evaluate_target_health = true
  }
}
