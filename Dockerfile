# ====== build args는 FROM보다 위에 선언 ======
# 빌드용 이미지와 실행용 이미지를 변수로 관리
ARG BUILDER_IMAGE=gradle:7.6.0-jdk17
ARG RUNTIME_IMAGE=amazoncorretto:17

# ============ (1) Builder Stage ============
# 빌더 스테이지 시작: 지정한 Gradle + JDK 환경을 사용
FROM ${BUILDER_IMAGE} AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# Gradle 캐시 위치 지정
ENV GRADLE_USER_HOME=/home/gradle/.gradle

# 프로젝트 파일 복사
COPY . /app

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# Gradle Wrapper를 사용해 애플리케이션 빌드
RUN ./gradlew clean build -x test


# ============ (2) Runtime Stage ============
FROM ${RUNTIME_IMAGE}

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# JVM 옵션 환경 변수 설정
ENV JVM_OPTS=""

# 빌더 단계에서 생성된 jar 파일만 복사
COPY --from=builder /app/build/libs/app.jar /app/app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 80

# 컨테이너 시작 시 jar 실행
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar /app/app.jar --server.port=80"]