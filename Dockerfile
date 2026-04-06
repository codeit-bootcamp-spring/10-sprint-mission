FROM amazoncorretto:17
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY . .

RUN chmod +x ./gradlew \
    && ./gradlew bootJar -x test --no-daemon \
    && cp build/libs/*.jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
