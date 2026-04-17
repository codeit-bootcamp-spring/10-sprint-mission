# ============ (1) Builder ============
# jar 만들기를 위한 스테이지
# 1. 베이스 이미지
FROM amazoncorretto:17 AS builder
# 2. 작업 디렉토리
WORKDIR /app

# 3. 파일 복사
# 캐시 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 4. 빌드
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성 미리 빌드
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# ============ (2) Runtime ============
# 1. 경량 이미지 사용
FROM amazoncorretto:17-alpine
# 2. 작업 디렉토리
WORKDIR /app
# 3. 빌드 스테이지에서 생성한 jar 파일만 복사(app.jar로 복사)
COPY --from=builder /app/build/libs/*.jar app.jar
# 4. 노출 포트
EXPOSE 80

# 5. 환경변수 설정
# 1) 프로젝트 이름
ENV  PROJECT_NAME=discodeit
# 2) 프로젝트 버전
ENV PROJECT_VERSION=1.2-M8
# 3) JVM 옵션(기본값은 빈 문자열)
# 메모리나 gc 옵션 설정가능
ENV JVM_OPTS=""

# 6. 컨테이너 실행
# 환경변수 실행을 위해 쉘 형태로 작성
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]
