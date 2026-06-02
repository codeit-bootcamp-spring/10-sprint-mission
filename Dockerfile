## Stage 1: Build (빌드 단계)
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app

# Gradle 캐싱을 위한 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 미리 다운로드
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

## Stage 2: Runtime (실행 단계)
FROM amazoncorretto:17-alpine
WORKDIR /app

# 서비스 포트
EXPOSE 80

# JVM 옵션 환경 변수 설정
ENV JVM_OPTS=""

# 프로젝트 정보 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# 환경 변수를 활용하여 빌드 결과물 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar .

# 환경 변수를 활용한 실행 명령어
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]