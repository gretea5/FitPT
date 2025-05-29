# Terraform 사용법

# 0 - KeyGen 필요, terraform.tfvars 생성, tfstate 저장할 S3 bucket 생성
## Public subnet, Private subnet RSA-Kwy 생성
### cd keys

## example 파일 확인
### mkdir -p ~/infra/keys

### ssh-keygen -t rsa -N "" -f ~/infra/keys/tf-fitpt

## terraform.tfvars 생성
### RDS, ElastiCache Password 설정 파일 생성

## tfstate 저장할 S3 bucket 생성, AWS CLI 또는 콘솔 수동 프로비저닝 -> 추후 자동화 가능
### aws s3api create-bucket --bucket fitpt-tf-state-bucket --region ap-northeast-2 --create-bucket-configuration LocationConstraint=ap-northeast-2

### aws dynamodb create-table --table-name terraform-locks --attribute-definitions AttributeName=LockID,AttributeType=S --key-schema AttributeName=LockID,KeyType=HASH --billing-mode PAY_PER_REQUEST


# 1 - IAM 등록 후 권한 주기 
### 커스텀 fitpt-Terraform-FullAccess
<details>
<summary>IAM 정책 JSON (펼치기)</summary>

<p>

<pre><code>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "NetworkAndCompute",
      "Effect": "Allow",
      "Action": [
        "ec2:*",
        "elasticloadbalancing:*"
      ],
      "Resource": "*"
    },
    {
      "Sid": "DatabaseAndCache",
      "Effect": "Allow",
      "Action": [
        "rds:*",
        "elasticache:*"
      ],
      "Resource": "*"
    },
    {
      "Sid": "StateStorageAndImage",
      "Effect": "Allow",
      "Action": [
        "s3:*",
        "ecr:*"
      ],
      "Resource": "*"
    },
    {
      "Sid": "DnsAndSslCertificate",
      "Effect": "Allow",
      "Action": [
        "route53:*",
        "acm:*"
      ],
      "Resource": "*"
    },
    {
      "Sid": "SSMAccess",
      "Effect": "Allow",
      "Action": [
        "ssm:*"
      ],
      "Resource": "*"
    },
    {
      "Sid": "IAMAccess",
      "Effect": "Allow",
      "Action": [
        "iam:*"
      ],
      "Resource": "*"
    }
  ]
}
</code></pre>

</p>
</details>

### 추가적으로 부족한 권한 주기 Ex) S3 권한, VPC, ECR, DynamoDB, EC2Container, Elasticache, RDS, IAM 등등

# 2 - Terraform 설치 (WSL2 및 Ubuntu)
### sudo apt update && sudo apt install -y wget unzip

### wget https://releases.hashicorp.com/terraform/1.4.6/terraform_1.4.6_linux_amd64.zip

### unzip terraform_1.4.6_linux_amd64.zip

### sudo mv terraform /usr/local/bin/

### terraform -v


# 2 - Terraform 작업용: aws configure 설정
### curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"

### unzip awscliv2.zip

### sudo ./aws/install

### aws --version

## aws 액세스키, 시크릿키 입력
### aws configure

## aws 액세스키, 시크릿키 입력 확인
### $ cat ~/.aws/credentials

### [default]
### aws_access_key_id = AKIA2xxxxxxxxx
### aws_secret_access_key = Wxxxxxxxxxxxxxxxxxxxxxxxxxx


# 3 - 9-route53.tf DNS 등록
## 0- Hosted Zone 자동 조회 DNS name 변경


# 4 - 12-ECR_Repo.tf 등록
## Elastic Container Registry local for each 문 안에 이름 맞게 수정


# 5 - Terraform 명령어
## Terraform 코드 폴더 이동
### cd ~/infra/terraform

## Terraform 문법 확인 교정
### terraform fmt

## Terraform 이니셜 필수
### terraform init

## Terraform 플랜으로 오류 확인 검증
### terraform plan

## Terraform 적용 (10-20분 소요, 설치 후 과금 시작)
### terraform apply

## Terraform 전부 삭제 (모든 설정 전부 삭제, 과금 요소 전부 삭제)
### terraform destroy