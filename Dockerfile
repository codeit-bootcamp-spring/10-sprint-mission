FROM amazoncorretto:17 AS builder

WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar -x test --no-daemon

RUN mkdir -p extracted \
    && java -Djarmode=layertools -jar build/libs/*.jar extract --destination extracted

FROM amazoncorretto:17-alpine AS runtime

WORKDIR /app

ENV JVM_OPTS=""
ENV SERVER_PORT=80

COPY --from=builder /workspace/extracted/dependencies/ ./
COPY --from=builder /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/extracted/application/ ./

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -Dserver.port=${SERVER_PORT} org.springframework.boot.loader.launch.JarLauncher"]