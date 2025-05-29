# Ansible 사용법

# 0 - pem key 권한 부족 시 해결법
### cd keys/
### chmod 600 tf-fitpt


# 1 - venv 가상환경 설정 및 앤서블 디펜던시 설치
## 실행 쉘 스크립트 실행
### chmod +x 0-setup.sh

## 실행
### ./0-setup.sh

## 가상 환경 접속
### source .venv/bin/activate


# 2 - ssm 전송 위한 버킷 설정
## ansible.cfg, hosts/ssm/group_vars/aws_ec2.yml 변경 필요


# 3 - 베스천 호스트 확인 (SSH 접속)
### ansible-inventory -i hosts/ssh/inventory.aws_ec2.yaml --list


# 4 - 베스천 호스트에 SSM Agent 설치
### ansible-playbook -i hosts/ssh/inventory.aws_ec2.yaml 1-setup-bastion.yaml   -u ubuntu -c ssh --private-key ../keys/tf-fitpt


# 5 - 프라이빗 인스턴스 관리
### ansible-playbook -i hosts/ssm/inventory.aws_ec2.yaml 2-setup-app.ymal


# 6 - 프라이빗 monitor 인스턴스 관리
### ansible-playbook -i hosts/ssm/inventory.aws_ec2.yaml 3-setup-monitor.yaml


# 7 - private subnet SSM Tunneling portforwording
## 예: Prometheus(9090) 터널링
### aws ssm start-session --target i-0ecddbb237f168fbf --document-name AWS-StartPortForwardingSession --parameters '{"portNumber":["9090"],"localPortNumber":["9090"]}'

## 예: Grafana(3000) 터널링
### aws ssm start-session --target i-0ecddbb237f168fbf --document-name AWS-StartPortForwardingSession --parameters '{"portNumber":["3000"],"localPortNumber":["3000"]}'
