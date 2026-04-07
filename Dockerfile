# Build stage
FROM amazoncorretto:17 AS builder
WORKDIR /build

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/ /app/libs/

EXPOSE 80

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar --server.port=80"]
