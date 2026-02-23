# ===== Build stage =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# (1) Gradle wrapper & build files 먼저 복사 (캐시 효율)
COPY gradlew ./
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

# gradlew 실행권한
RUN chmod +x gradlew

# (2) 소스 복사
COPY src src

# (3) 빌드 (테스트/체크 제외 + production 프로퍼티)
RUN ./gradlew --no-daemon clean bootJar -x test -x check -Pproduction

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Railway는 PORT 환경변수를 줌
ENV PORT=8080
EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
