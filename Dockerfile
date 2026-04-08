# Stage 1: Build stage
FROM amazoncorretto:17 AS builder
WORKDIR /app

# 설정을 먼저 복사하여 종속성 캐싱 활용
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 먼저 다운로드 (내용 변경 없을 시 캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app

# 빌드 결과물만 복사 (용량 최적화)
# '*-plain.jar'를 제외한 실행 가능한 jar만 가져옵니다.
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 80

ENV JVM_OPTS=""

# 실행 명령
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
