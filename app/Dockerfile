# BUILDER:  빌드 전용 환경
FROM gradle:8.14-jdk17 AS builder
WORKDIR /builder

# 최소 메타 파일만 먼저 복사
COPY gradlew settings.gradle build.gradle ./
COPY gradle/ gradle/

# 도커 캐시 마운트를 활용한 의존성 다운로드
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon dependencies

# 실제 자바 소스 코드만 콕 집어서 복사
COPY src/ src/

# clean 제거, 캐시 마운트를 활용해 빠르게 Jar 생성
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar

# RUNTIME: Amazon Linux 2023 기반의 서버용 JRE
FROM amazoncorretto:17-al2023-headless

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=3.0-M12
ENV JVM_OPTS=""

WORKDIR /app

# builder 스테이지에서 생성된 순수 결과물 jar 딱 하나만 탈취해 오기
COPY --from=builder /builder/build/libs/$PROJECT_NAME-$PROJECT_VERSION.jar app.jar

# 서비스 노출 포트
EXPOSE 8080

# sh -c: 쉘을 실행해서 환경변수 읽어오기
#  exec: 현재 쉘 프로세스를 java 프로세스로 완전히 교체(즉, 쉘은 java를 실행하고 실행종료)
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]