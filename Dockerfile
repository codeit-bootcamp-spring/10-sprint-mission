# 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew bootJar -x test


# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

ENV JVM_OPTS=""

# 버전과 무관하게 부트 JAR 단일 파일을 app.jar로 복사
COPY --from=builder /app/build/libs/discodeit-*.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]
