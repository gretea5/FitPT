########################################
# Application Load Balancer (ALB)
########################################

resource "aws_lb" "app_alb" {
  name               = "${var.project_name}-${var.customer_id}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = [aws_subnet.public.id]

  tags = {
    Name = "${var.project_name}-${var.customer_id}-alb"
  }
}

########################################
# ALB Security Group
########################################

resource "aws_security_group" "alb_sg" {
  name   = "${var.project_name}-${var.customer_id}-alb-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.customer_id}-alb-sg"
  }
}

########################################
# Target Group (App Service 9090) -> 외부 통신
########################################

resource "aws_lb_target_group" "app_tg" {
  name     = "${var.project_name}-${var.customer_id}-app-tg"
  port     = 9090
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/api-health"
    protocol            = "HTTP"
    matcher             = "200-399"

  }
}

  resource "aws_lb_target_group" "admin_tg" {
  name     = "${var.project_name}-${var.customer_id}-admin-tg"
  port     = 9091
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/admin-health"
    matcher             = "200-399"
  }
}


########################################
# ALB Listener (Port 80 → TargetGroup 9090) -> 내부 통신신
# HTTPS Listener (Port 443) - Certificate 필요
# Listener + Rule (Host or Path Based Forwarding)
########################################

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = aws_lb.app_alb.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type = "redirect"
    redirect {
        port        = "443"
        protocol    = "HTTPS"
        status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener" "https_listener" {
  load_balancer_arn = aws_lb.app_alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.acm_certificate_arn

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.app_tg.arn
  }
}

resource "aws_lb_listener_rule" "api_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app_tg.arn
  }

  condition {
    path_pattern {
      values = ["/swagger-ui/*", "/api/*"]
    }
  }
}

resource "aws_lb_listener_rule" "admin_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority     = 200

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.admin_tg.arn
  }

  condition {
    path_pattern {
      values = ["/admin/*"]
    }
  }
}
