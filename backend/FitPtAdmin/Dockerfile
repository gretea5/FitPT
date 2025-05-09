# 1. Java 베이스 이미지
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 복사
COPY build/libs/FitPtAdmin-0.0.1-SNAPSHOT.jar app.jar

# ✅ 4. application.yaml 복사
COPY src/main/resources/application.yaml ./application.yaml

# 5. 포트 노출
EXPOSE 9091

# 6. 실행 명령어: 외부 YAML 사용하도록 -Dspring.config.location
ENTRYPOINT ["java", "-Dspring.config.location=classpath:/,file:./application.yaml", "-jar", "app.jar"]
