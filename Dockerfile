# ===== Build stage =====
FROM amazoncorretto:17 AS build
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# Gradle Wrapper 및 빌드 설정 파일 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# Gradle 의존성 캐시
RUN ./gradlew --no-daemon dependencies || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./gradlew --no-daemon clean bootJar -x test -x check -Pproduction

# ===== Run stage =====
FROM amazoncorretto:17-alpine
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# build stage에서 생성된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar

EXPOSE 80

CMD ["sh", "-c", "java ${JVM_OPTS} -Dserver.port=80 -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]