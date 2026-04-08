# ====== build args는 반드시 FROM보다 위에 선언 ======
# 도커 빌드 시 사용할 빌드/런타임 이미지 이름을 변수로 지정
# ARG는 선언된 이후부터만 유효하므로 반드시 FROM보다 위에 있어야 함
ARG BUILDER_IMAGE=gradle:7.6.0-jdk17
ARG RUNTIME_IMAGE=amazoncorretto:17.0.7-alpine

# ============ (1) Builder ============
# 빌더 스테이지 시작: 지정한 Gradle + JDK 환경을 사용
FROM ${BUILDER_IMAGE} AS builder

# 루트 권한으로 변경 (권한 설정/폴더 생성 작업을 위해)
USER root
# 애플리케이션 작업 디렉토리 설정
WORKDIR /app
# Gradle 캐시 디렉토리 경로를 환경 변수로 설정 (빌드 속도 향상)
ENV GRADLE_USER_HOME=/home/gradle/.gradle
# Gradle 캐시 디렉토리와 앱 디렉토리 소유자를 gradle 유저로 변경
RUN mkdir -p $GRADLE_USER_HOME && chown -R gradle:gradle /home/gradle /app
# gradle 유저로 변경 (보안 및 권한 문제 방지)
USER gradle

# Gradle Wrapper 스크립트 복사 (빌드 실행에 필요)
COPY --chown=gradle:gradle gradlew ./
# gradle 폴더 복사 (wrapper 설정 및 실행 환경)
COPY --chown=gradle:gradle gradle ./gradle
# Gradle 설정 파일 복사 (빌드 스크립트)
COPY --chown=gradle:gradle build.gradle settings.gradle ./
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드하여 캐시 활용 (코드 변경 없이 재사용 가능)
RUN ./gradlew --no-daemon --refresh-dependencies dependencies || true

# 실제 소스코드 복사 (이 시점 이후 변경 시 빌드 다시 수행됨)
COPY --chown=gradle:gradle src ./src
# 애플리케이션 빌드 (테스트 제외, 속도 향상)
RUN ./gradlew clean build --no-daemon --no-parallel -x test

# ============ (2) Runtime ============
# 런타임 스테이지: 빌드 결과 실행에 필요한 최소한의 경량 이미지 사용
FROM ${RUNTIME_IMAGE}
# 앱 실행 디렉토리 지정
WORKDIR /app

# 프로젝트 정보 및 옵션 변수
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

# 빌드 스테이지에서 생성한 JAR 파일만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar
# 애플리케이션이 사용하는 포트 노출
EXPOSE 80
# 컨테이너 시작 시 JAR 실행
ENTRYPOINT ["sh", "-c", "exec java ${JVM_OPTS} -jar app.jar"]
