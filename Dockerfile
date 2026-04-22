# ====== ARG 선언 ======
ARG BUILDER_IMAGE=gradle:7.6.0-jdk17
ARG RUNTIME_IMAGE=amazoncorretto:17.0.7-alpine

# ============ Builder Stage ============
FROM ${BUILDER_IMAGE} AS builder

USER root
WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle
RUN mkdir -p $GRADLE_USER_HOME && chown -R gradle:gradle /home/gradle /app

USER gradle

# ====== 레이어 캐시 최적화 ======
# 변동이 적은 라이브러리 환경부터 구성하여 캐시를 활용
COPY --chown=gradle:gradle gradlew ./
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle build.gradle settings.gradle ./

RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드하여 레이어 캐싱 (소스 변경 시에도 다운로드 생략)
RUN ./gradlew dependencies --no-daemon || true

# ====== 소스 빌드 ======
COPY --chown=gradle:gradle src ./src
# 애플리케이션 빌드 (테스트 제외, 속도 향상)
RUN ./gradlew clean bootJar --no-daemon -x test

# ============ Runtime Stage ============
FROM ${RUNTIME_IMAGE}
WORKDIR /app

# ====== 보안 강화 ======
# 런타임에서도 root가 아닌 일반 유저로 앱을 실행하도록 설정
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 일반 유저에게 권한 부여
RUN mkdir -p /app/.logs && chown -R appuser:appgroup /app

# ====== 멀티 스테이지 최적화 ======
COPY --from=builder --chown=appuser:appgroup /app/build/libs/app.jar /app/app.jar

USER appuser

# 애플리케이션 포트 노출 및 환경 변수 설정
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

# 컨테이너 시작 시 JAR 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]