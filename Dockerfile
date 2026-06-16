# syntax=docker/dockerfile:1.7

ARG BUILDPLATFORM=linux/amd64
ARG PROJECT_NAME=discodeit
ARG PROJECT_VERSION=1.2-M8

FROM --platform=$BUILDPLATFORM amazoncorretto:17 AS builder
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./

RUN chmod +x ./gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon || true

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar -x test --no-daemon

FROM amazoncorretto:17
WORKDIR /app

ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/*.jar /app/discodeit-1.2-M8.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar /app/discodeit-1.2-M8.jar"]
