# 1. 베이스 이미지 설정
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar


# 4. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]