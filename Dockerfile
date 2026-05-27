FROM amazoncorretto:17 AS builder
WORKDIR /app

# 의존성 파일만 먼저 복사 (캐시 레이어 생성)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew .
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사 후 빌드
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 80
ENV JVM_OPTS=""
ENV SERVER_PORT=80

CMD sh -c "java $JVM_OPTS -jar app.jar"