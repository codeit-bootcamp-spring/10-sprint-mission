# builder 이미지
FROM amazoncorretto:17 AS builder

# 컨테이너 내부 작업 디렉터리
WORKDIR /app

# Gradle Wrapper와 설정 파일을 먼저 복사. 캐싱 목적
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

# Linux 컨테이너에서 gradlew를 실행할 수 있도록 권한을 부여.
RUN chmod +x ./gradlew

# 프로젝트 의존성을 먼저 다운.
# 이후 소스만 바뀌면 Docker 캐시를 재사용 가능
RUN ./gradlew dependencies --no-daemon

# 실제 애플리케이션 소스 코드를 복사.
COPY src ./src

# 테스트는 제외하고 실행 가능한 Spring Boot jar를 생성.
RUN ./gradlew bootJar -x test --no-daemon


# 빌드 도구 없이 JRE/JDK 기반의 가벼운 이미지에서 jar만 실행.
FROM amazoncorretto:17-alpine3.21

# 실행 컨테이너 내부 작업 디렉터리
WORKDIR /app

# JVM 옵션을 외부에서 주입할 수 있도록 환경변수로.
ENV JVM_OPTS=""

# builder 단계에서 생성된 jar 파일을 app.jar 이름으로 복사.
# build.gradle의 version이 바뀌어도 *.jar로 복사하므로 Dockerfile 수정 x.
COPY --from=builder /app/build/libs/*.jar app.jar

#docker-compose의 nginx가 접근
EXPOSE 8080

# 컨테이너 시작 시 Spring Boot 애플리케이션을 실행.
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]