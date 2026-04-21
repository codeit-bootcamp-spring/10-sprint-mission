# 도커 빌드 시 사용할 빌드/런타임 이미지 이름을 변수로 지정
ARG BUILDER_IMAGE=amazoncorretto:17
# 실행 시에는 무거운 빌드 파일들은 버리고 실행에만 필요한 jre환경이다
ARG RUNTIME_IMAGE=amazoncorretto:17.0.7-alpine

# ============ (1) Builder ============
FROM ${BUILDER_IMAGE} AS builder
# 빌드 권한 일반 유저로
USER root
WORKDIR /app

# 내 로컬의 gradlew실행파일을 컨테이너 현재 디렉토리(/app)에 복사한다
COPY gradlew ./
# 내 로컬의 gradle파일을 컨테이너 /app/gradle에 복사한다,
COPY gradle ./gradle
# 내 로컬의 build.gradle과 setting.gradle을 컨테이너 현재 디렉토리(/app)에 복사한다
COPY build.gradle settings.gradle ./

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드하여 캐시 활용 (코드 변경 없이 재사용 가능)
RUN ./gradlew dependencies --no-daemon

# 로컬의 src를 컨테이너의 현재  디렉토리 안의 폴더 src에 복사
COPY src ./src
# 애플리케이션 빌드 (테스트 제외, 속도 향상)
RUN ./gradlew clean build --no-daemon --no-parallel -x test

# ============ (2) Runtime ============
# 런타임 스테이지: 빌드 결과 실행에 필요한 최소한의 경량 이미지 사용
FROM ${RUNTIME_IMAGE}
# 앱 실행 디렉토리 지정
# 실행 시에는 빌드 스테이지에서 만든 무거운 app이 지워짐
WORKDIR /app
# 환경변수
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
# 빌드 스테이지에서 생성한 JAR 파일만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar
# 애플리케이션이 사용하는 포트 노출
EXPOSE 80
# Spring Boot 프로필을 운영(prod)으로 설정
ENV SPRING_PROFILES_ACTIVE=prod
# 컨테이너 시작 시 JAR 실행, sh -c 는 환경변수를 쉘환경에서 인식시키기 위해서 추가함
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar $PROJECT_NAME-$PROJECT_VERSION.jar"]
