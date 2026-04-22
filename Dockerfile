#빌드 단계
#변경이 적은 Gradle 관련 파일을 먼저 복사해서 Docker Layer cache를 활용
FROM gradle:8.11.1-jdk17 AS builder

#빌드 작업 디렉토리
WORKDIR /build

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

#Gradle 관련 파일 먼저 복사
#이 파일들은 자주 안바뀌므로 먼저 복사하면 의존성 다운로드 레이어 캐시 적중률이 올라간다.
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

#아직 src없어서 build 실패할 수 있다.
#목적은 의존성 미리 다운로드해서 캐시하는거라 실패해도 다음 단계 넘어가도록 처리
RUN ./gradlew --no-daemon dependencies || true

#실제 소스 복사
#소스는 변경 빈도가 높으므로 나중에 복사한다.
#코드만 바뀌는거라면 앞단 의졵성 레이어는 재사용 가능하다
COPY src ./src

#Spring Boot 실행 jar 생성
#이미지 빌드 속도위해 테스트 제외
RUN ./gradlew --no-daemon clean bootJar -x test

#런타임 단계
#실제 실행에는 Gradle이나 전체 JDK 필요없다.
#JRE에서 jar만 실행한다.
FROM eclipse-temurin:17-jre AS runtime

#실행 작업 디렉토리
WORKDIR /app

# JVM 실행 옵션 (기본값: 빈 문자열)
ENV JAVA_TOOL_OPTIONS=""

# builder 단계에서 만든 jar를 runtime 이미지로 복사
COPY --from=builder /build/build/libs/*.jar /app/app.jar

#애플리케이션 포트
EXPOSE 80

# 컨테이너 시작 시 Spring Boot 실행
ENTRYPOINT ["sh", "-lc", "exec java $JAVA_TOOL_OPTIONS -jar /app/app.jar --server.port=80"]