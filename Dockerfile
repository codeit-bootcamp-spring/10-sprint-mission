# ==========================================
# 1. 빌드 스테이지 (Build Stage)
# ==========================================
# JDK(자바 개발 도구)가 포함된 무거운 이미지를 사용합니다.
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 환경 파일들 복사 (캐시 활용을 위해 소스 코드보다 먼저 복사)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Windows 환경에서 작성된 gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성(라이브러리)을 먼저 다운로드 (이후 소스코드가 바뀌어도 이 부분은 캐시되어 빌드가 빨라집니다)
RUN ./gradlew dependencies --no-daemon || true

# 실제 소스 코드 복사
COPY . .

# 애플리케이션 빌드 (테스트 코드는 제외하여 빌드 속도 향상)
RUN ./gradlew clean build -x test --no-daemon

# ==========================================
# 2. 실행 스테이지 (Run Stage)
# ==========================================
# JRE(자바 실행 환경)만 포함된 가벼운 이미지를 사용하여 최종 용량을 줄입니다.
FROM amazoncorretto:17-alpine

WORKDIR /app

# 빌드 스테이지(builder)에서 완성된 .jar 파일만 쏙 빼와서 app.jar로 이름을 바꿔서 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 컨테이너가 켜질 때 실행할 명령어 지정
ENTRYPOINT ["java", "-jar", "app.jar"]
