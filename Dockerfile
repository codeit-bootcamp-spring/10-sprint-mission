FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew build -x test --no-daemon

FROM amazoncorretto:17-al2-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/discodeit-1.2-M8.jar build/libs/discodeit-1.2-M8.jar

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]