FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

FROM amazoncorretto:17-alpine3.21

WORKDIR /app

ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]