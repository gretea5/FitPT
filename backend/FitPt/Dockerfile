# 1. 베이스 이미지 설정 (Java 17 slim)
FROM openjdk:17-jdk-slim

# 2. 시스템 타임존 설정
ENV TZ=Asia/Seoul

# 3. 작업 디렉토리 설정
WORKDIR /app

# 4. 빌드된 Spring Boot jar 파일 복사
COPY build/libs/FitPt-0.0.1-SNAPSHOT.jar app.jar

# 5.-1 외부 application.yaml 파일을 컨테이너 내부로 복사
COPY src/main/resources/application.yaml ./application.yaml

COPY src/main/resources/application-local.yaml ./application-local.yaml

# 5-2 firebase 서비스 키 파일 복사
COPY firebase/firebase_service_key.json ./firebase/firebase_service_key.json

# 6. EC2 내부 포트 오픈 (application.yaml에서 9090 설정했으므로)
EXPOSE 9090

# 7. Spring Boot 실행 시 외부 application.yaml 을 강제 참조
#ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar", "--spring.config.additional-location=file:/app/application.yaml"]
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar", "--spring.config.additional-location=file:/app/application-local.yaml", "--spring.profiles.active=local"]
