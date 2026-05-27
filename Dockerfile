# ==========================================
# Stage 1: Build the application (JDK)
# ==========================================
# 베이스 이미지
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉토리 설정: 컨테이너 내부에서 애플리케이션이 위치할 디렉토리를 /build으로 지정
WORKDIR /build

# 잘 안 바뀌는 파일부터 먼저 복사 (레이어 캐싱 전략)
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사 전에 의존성(라이브러리)만 미리 다운로드 (이렇게 하면 코드가 바뀌어도 라이브러리 다운로드 시간은 캐시로 패스됨)
RUN ./gradlew dependencies --no-daemon

# 소스 코드를 복사
COPY src src

# 프로젝트 빌드 후 jar 파일 생성
RUN ./gradlew bootJar --no-daemon

# ==========================================
# Stage 2: Runtime environment (JRE)
# ==========================================
# 실제 실행할 때는 가벼운 JRE(실행 환경)만 있으면 되니 멀티 스테이지 빌드로 최종 이미지를 만듦
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 멀티 스테이지: 빌더 단계에서 생성된 jar 파일을 최종 이미지로 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 컨테이너 실행 (JAVA_TOOL_OPTIONS 환경변수는 알아서 매핑)
ENTRYPOINT ["java", "-jar", "app.jar"]