FROM amazoncorretto:17-alpine

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV SPRING_PROFILES_ACTIVE=prod

# gradle wrapper 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

# 소스 복사
COPY . .

# 빌드 (테스트 제외)
RUN ./gradlew bootJar -x test

EXPOSE 80

# JVM 옵션 직접 명시 (핵심)
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:MaxMetaspaceSize=128m", "-XX:+UseSerialGC", "-jar", "build/libs/discodeit-1.2-M8.jar", "--server.port=80"]