# ===== 빌드 스테이지 =====
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 의존성 캐시 레이어 (소스 변경 시에도 의존성은 재다운로드 안 함)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src src
RUN ./gradlew build -x test --no-daemon

# ===== 런타임 스테이지 =====
FROM amazoncorretto:17-al2023-headless

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 스테이지에서 jar만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]