FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM amazoncorretto:17

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/app.jar

EXPOSE 80

CMD ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar --spring.profiles.active=prod --server.port=80"]