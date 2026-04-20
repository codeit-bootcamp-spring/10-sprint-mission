FROM amazoncorretto:17 AS builder
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JVM_OPTS=""
EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]