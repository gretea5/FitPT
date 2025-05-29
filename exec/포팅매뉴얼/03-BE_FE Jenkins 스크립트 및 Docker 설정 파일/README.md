# 03-BE/FE Jenkins 스크립트 및 Docker 설정 파일

## Jenkins pipeline 정리

**dev-be**  :  백엔드 기능 통합 개발용 브랜치  **( 구축 완료 )**

**dev-fe   :**  프론트엔드 기능 통합 개발용 브랜치  **( 구축 완료 )**

**devlop**  :  dev-be와 dev-fe를 병합하여 최종 점검하는 브랜치

**master  :**  실제 운영 브랜치 

*백엔드 개발 브랜치로 변경 및 merge request 명령을 받으면 자동빌드 되도록 해야됨. ( 현재는 테스트 )  **'feature/infra'  →  'dev-be'**  

## 9. BackEnd 배포 ( dev-fe )

**아래 Jenkins plugin 설치하기**

Generic Webhook Trigger Plugin

GitLab API Plugin

GitLab Plugin

Stage View  →  배포 과정 쉽게 볼 수 있음

### 9.1 app/docker-compose.yml 작성

```coffeescript
services:
  app:
    build:
      context: ../../../backend/monthlyzip
      dockerfile: Dockerfile
      args:
        PROFILE: prod  # 빌드시 ARG PROFILE에 전달할 값
    image: monthlyzip-app
    container_name: monthlyzip-app
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    restart: always
    networks:
      - app-network
networks:
  app-network:
    external: true

```

### 9.2. DockerFile 작성

```coffeescript

# backend/monthlyzip/Dockerfile

FROM amazoncorretto:17
ARG JAR_FILE=./build/libs/monthlyzip-0.0.1-SNAPSHOT.jar
ARG PROFILE
ENV SPRING_PROFILES_ACTIVE=${PROFILE}
WORKDIR /app
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application-secret.yml /app/application-secret.yml
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
```

### 9.3. Jenkins pipeline을 이용한 자동배포 스크립트

```coffeescript
pipeline {
    agent any
    stages {
        stage('Check Branch') {
            steps {
                script {
                    // def targetBranch = 'dev-be'
                    def targetBranch = env.gitlabTargetBranch ?: env.BRANCH_NAME
                    if (targetBranch != null && targetBranch != 'dev-be') {
                        currentBuild.result = 'ABORTED'
                        error("This pipeline only runs for merge requests to dev-be branch")
                    }
                }
            }
        }
        stage('CheckOut') {
            steps {
                echo 'Start CheckOut monthlyzip project...'
                git branch: 'dev-be',
                    credentialsId: 'account',
                    url: 'https://lab.ssafy.com/s12-fintech-finance-sub1/S12P21D109.git'
								sh 'ls -R infra/docker/app'  // 파일 목록 출력
                sh '''
				            pwd
				            ls -R
				        '''
                echo 'CheckOut finished!'
            }
        }
        stage('Build') {
            steps {
                echo 'Start building monthlyzip project...'
                dir('backend/monthlyzip') {
                    withCredentials([file(credentialsId: 'application-secret', variable: 'SECRET_FILE')]) {
                        sh """
                            cp -f "$SECRET_FILE" src/main/resources/application-secret.yml
                            cat src/main/resources/application-secret.yml
                        """
                    }
                    sh '''
                        chmod +x ./gradlew
                        ./gradlew clean build -x test
                    '''
                }
                echo 'Build finished!'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    dir('infra/docker/app') {
                        // application-secret.yml 파일 복사
                        withCredentials([file(credentialsId: 'application-secret', variable: 'SECRET_FILE')]) {
                            sh """
                                cp -f "\$SECRET_FILE" application-secret.yml
                                chmod 600 application-secret.yml
                            """
                        }

                        // 기존 컨테이너 중지 및 제거
                        sh "docker compose down app || true"
                        
                        // 새 이미지 빌드 및 컨테이너 시작
                        sh "docker compose build --no-cache app"
                        sh "docker compose up -d app"
                        
                        sh "sleep 20" // 서버가 완전히 시작될 때까지 잠시 대기

                        // 배포 완료 후 시크릿 파일 삭제
                        sh "rm -f application-secret.yml"

                        echo 'Deploy finished!'
                    }
                }
            }
        }
        
    }
    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            dir('infra/docker/app') {
                sh 'docker compose logs app'
            }
        }
        always {
            echo 'Cleaning up workspace'
            cleanWs()
        }
    }
}
```

## 10. FrontEnd 배포  ( dev-fe )

**아래 Jenkins plugin 설치하기**

 NodeJs Plugin

### 10.1. frontend/docker-compose.yml 작성

```coffeescript
services:
  react:
    image: react
    build:
      context: ../../../frontend/monthly-zip
      dockerfile: Dockerfile
    container_name: react
    ports:
      - "3000:80"
    networks:
      - app-network
    restart: always

networks:
  app-network:
    external: true

```

### 10.2. DockerFile 작성

```coffeescript
# 빌드 단계
FROM node:22.13.0-alpine as builder

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 설치
COPY package*.json ./
RUN npm install

# 앱 소스 복사 및 빌드 
COPY . .
RUN npm run build   

# 프로덕션 단계
FROM nginx:alpine

# 빌드 결과물 복사
COPY --from=builder /app/build /usr/share/nginx/html

# 포트 설정
EXPOSE 80

# 실행 명령어
CMD ["nginx", "-g", "daemon off;"]
```

### 10.3. Nginx.conf 작성

- nginx.conf / (/nginx_conf 폴더 내 파일)
    
    ```
    user nginx;
    worker_processes auto;
    error_log /var/log/nginx/error.log warn;
    pid /var/run/nginx.pid;
    
    events {
        worker_connections 1024;
    }
    
    http {
        include /etc/nginx/mime.types;
        default_type application/octet-stream;
        sendfile on;
        keepalive_timeout 65;
        include /etc/nginx/conf.d/*.conf;
    
    }
    ```
    
- default.conf / (/nginx_conf/conf.d 폴더 내 파일)
    
    ```
    server {
        listen 80;
        server_name k12s208.p.ssafy.io;
    
        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }
        return 301 https://$host$request_uri;
    }
    
    server {
        listen 443 ssl;
        server_name k12s208.p.ssafy.io;
    
        ssl_certificate /etc/letsencrypt/live/k12s208.p.ssafy.io/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/k12s208.p.ssafy.io/privkey.pem;
    
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_prefer_server_ciphers on;
    
        location / {
            proxy_pass http://fitpt-web:80;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        location /api/ {
            proxy_pass http://fitpt-app:9090/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Port $server_port;
        }
    
        location /swagger-ui/ {
            proxy_pass http://fitpt-app:9090/swagger-ui/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
    
            proxy_redirect off;
            sub_filter_once off;
            sub_filter 'href="/' 'href="/swagger-ui/';
            sub_filter 'src="/' 'src="/swagger-ui/';
    }
    
     location /v3/api-docs {
            proxy_pass http://fitpt-app:9090/v3/api-docs;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Port $server_port;
    }
    
        location /admin/ {
            proxy_pass http://fitptadmin-app:9091/admin/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Port $server_port;
        }
    
        location /jenkins/ {
            proxy_pass http://jenkins:8080/jenkins/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
    
            proxy_buffering off;
            proxy_request_buffering off;
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 120s;
    
            sub_filter_once off;
            sub_filter 'href="/' 'href="/jenkins/';
            sub_filter 'src="/' 'src="/jenkins/';
        }
    
        location /jenkins/static/ {
            proxy_pass http://jenkins:8080/jenkins/static/;
        }
    
        location /jenkins/userContent/ {
            proxy_pass http://jenkins:8080/jenkins/userContent/;
        }
    
        location /jenkins/favicon.ico {
            proxy_pass http://jenkins:8080/jenkins/favicon.ico;
        }
    
        location /portainer/ {
            proxy_pass https://portainer:9443/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Port $server_port;
    
            proxy_redirect off;
            sub_filter_once off;
            sub_filter 'href="/' 'href="/portainer/';
            sub_filter 'src="/' 'src="/portainer/';
        }
    }
    
    ```
    

### 10.4. Jenkins pipeline를 이용하여 배포

1. BE-app
    
    ```
    pipeline {
        agent any
    
        environment {
            EC2_IP = ''
            DOCKER_IMAGE_NAME = 'docker_hub_credentials/fitpt'
            DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        }
    
        stages {
            stage('Checkout develop') {
                steps {
                    git branch: 'develop', url: 'git_URL', credentialsId: 'SSAFY_GITLAB'
                }
            }
    
            stage('Prepare Secrets') {
                steps {
                    withCredentials([
                        file(credentialsId: 'APP_APPLICATION_YAML', variable: 'APP_APPLICATION_YAML'),
                        file(credentialsId: 'APP_APPLICATION_LOCAL_YAML_FILE', variable: 'APP_APPLICATION_LOCAL_YAML_FILE'),
                        file(credentialsId: 'FIREBASE_SERVICE_KEY', variable: 'FIREBASE_SERVICE_KEY'),
                        file(credentialsId: 'FITPT_APP_DOCKERFILE', variable: 'FITPT_APP_DOCKERFILE')
                    ]) {
                        sh """
                            chmod -R u+w backend/FitPt
                            cp ${FITPT_APP_DOCKERFILE} backend/FitPt/Dockerfile
                            cp ${FIREBASE_SERVICE_KEY} backend/FitPt/firebase/firebase_service_key.json
                            cp ${APP_APPLICATION_YAML} backend/FitPt/src/main/resources/application.yaml
                            cp ${APP_APPLICATION_LOCAL_YAML_FILE} backend/FitPt/src/main/resources/application-local.yaml
                        """
                    }
                }
            }
    
            stage('Build JAR after yaml override') {
                steps {
                    dir('backend/FitPt') {
                        sh """
                            chmod +x gradlew
                            ./gradlew clean build -x test
                        """
                    }
                }
            }
            
            stage('Build & Push Docker Image') {
                steps {
                    withCredentials([
                        usernamePassword(credentialsId: 'fitpt-dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')
                    ]) {
                        dir('backend/FitPt') {
                            sh """
                                docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                                docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest
            
                                echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin
                                docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                                docker push ${DOCKER_IMAGE_NAME}:latest
                            """
                        }
                    }
                }
            }
    
            stage('Deploy to EC2') {
                steps {
                    sshagent(['SSH_KEY']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                                docker pull ${DOCKER_IMAGE_NAME}:latest
    
                                docker stop fitpt-app || true
                                docker rm fitpt-app || true
    
                                docker run -d --name fitpt-app --network fitpt-net -p 9090:9090 ${DOCKER_IMAGE_NAME}:latest
                            '
                        """
                    }
                }
            }
        }
    
        post {
            success {
                echo 'Deployment Success!'
            }
            failure {
                echo 'Deployment Failed!'
            }
        }
    }
    
    ```
    
2. BE-admin
    
    ```
    pipeline {
        agent any
    
        environment {
            EC2_IP = ''
            DOCKER_IMAGE_NAME = 'docker_hub_credentials/fitptadmin'
            DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        }
    
        stages {
            stage('Checkout develop') {
                steps {
                    git branch: 'develop', url: 'git_URL', credentialsId: 'SSAFY_GITLAB'
                }
            }
    
            stage('Prepare Secrets') {
                steps {
                    withCredentials([
                        file(credentialsId: 'ADMIN_APPLICATION_YAML', variable: 'APPLICATION_YAML_FILE'),
                        file(credentialsId: 'FITPT_ADMIN_DOCKERFILE', variable: 'DOCKERFILE_SECRET')
                    ]) {
                        sh """
                            chmod -R u+w backend/FitPtAdmin
                            cp ${DOCKERFILE_SECRET} backend/FitPtAdmin/Dockerfile
                            cp ${APPLICATION_YAML_FILE} backend/FitPtAdmin/src/main/resources/application.yaml
                        """
                    }
                }
            }
            
            stage('Build JAR after yaml override') {
                steps {
                    dir('backend/FitPtAdmin') {
                        sh """
                            chmod +x gradlew
                            ./gradlew clean build -x test
                        """
                    }
                }
            }
            
            stage('Build & Push Docker Image') {
                steps {
                    withCredentials([
                        usernamePassword(credentialsId: 'fitpt-dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')
                    ]) {
                        dir('backend/FitPtAdmin') {
                            sh """
                                docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                                docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest
            
                                echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin
                                docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                                docker push ${DOCKER_IMAGE_NAME}:latest
                            """
                        }
                    }
                }
            }
    
            stage('Deploy to EC2') {
                steps {
                    sshagent(['SSH_KEY']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                                docker pull ${DOCKER_IMAGE_NAME}:latest
    
                                docker stop fitptadmin-app || true
                                docker rm fitptadmin-app || true
    
                                docker run -d --name fitptadmin-app --network fitpt-net -p 9091:9091 ${DOCKER_IMAGE_NAME}:latest
                            '
                        """
                    }
                }
            }
        }
    
        post {
            success {
                echo 'Deployment Success!'
            }
            failure {
                echo 'Deployment Failed!'
            }
        }
    }
    
    ```
    
3. FE
    
    ```
    pipeline {
        agent any
    
        environment {
            EC2_IP = ''
            DOCKER_IMAGE_NAME = 'docker_hub_credentials/fitptweb'
            DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        }
    
        tools {
            nodejs 'nodejs'
        }
      
        stages {
            stage('Checkout develop') {
                steps {
                    git branch: 'frontend', url: 'git_URL', credentialsId: 'SSAFY_GITLAB'
                }
            }
    
            stage('Prepare Secrets') {
                steps {
                    withCredentials([
                        file(credentialsId: 'FRONTEND_ENV_FILE', variable: 'ENV_FILE')
                    ]) {
                        sh """
                            chmod -R u+w fitpt-landing
                            cp ${ENV_FILE} fitpt-landing/.env
                        """
                    }
                }
            }
    
            stage('Install Dependencies') {
              steps {
                dir('fitpt-landing') {
                  sh 'npm ci'
                }
              }
            }
        
            stage('Build React App') {
              steps {
                dir('fitpt-landing') {
                  sh 'npm run build'
                }
              }
            }
                
            stage('Build & Push Docker Image') {
                steps {
                    withCredentials([
                        usernamePassword(credentialsId: 'fitpt-dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')
                    ]) {
                        dir('fitpt-landing') {
                            sh """
                                docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                                docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest
            
                                echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin
                                docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                                docker push ${DOCKER_IMAGE_NAME}:latest
                            """
                        }
                    }
                }
            }
    
            stage('Deploy to EC2') {
                steps {
                    sshagent(['SSH_KEY']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                                docker pull ${DOCKER_IMAGE_NAME}:latest
    
                                docker stop fitpt-web || true
                                docker rm fitpt-web || true
    
                                docker run -d --name fitpt-web --network fitpt-net -p 3000:80 ${DOCKER_IMAGE_NAME}:latest
                            '
                        """
                    }
                }
            }
        }
    
        post {
            success {
                echo 'Deployment Success!'
            }
            failure {
                echo 'Deployment Failed!'
            }
        }
    }
    
    ```