FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN sed -i 's/\r$//' gradlew

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon || true

COPY . .

RUN sed -i 's/\r$//' gradlew

RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:17-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
