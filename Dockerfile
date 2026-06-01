# 1단계: 빌드
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 의존성 캐시를 위해 gradle 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# 소스코드 복사 후 빌드
COPY src src
RUN ./gradlew clean build -x test --parallel --no-daemon

# 2단계: 런타임
FROM amazoncorretto:17-al2023

WORKDIR /app

COPY --from=builder /app/build/libs/app.jar app.jar

ENV JVM_OPTS=""

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]