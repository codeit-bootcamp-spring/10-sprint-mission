# 베이스 이미지: 요구사항에 명시된 Amazon Corretto 17의 특정 해시값을 정확히 타겟팅
FROM amazoncorretto:17@sha256:8929bdc3e2be20250ae46b3bc1dc361fcd637cd189ec02174251b0e499a22fad

# 작업 디렉토리 설정: 컨테이너 내부에서 애플리케이션이 위치할 디렉토리를 /app으로 지정
WORKDIR /app

# 호스트(내 pc)의 파일들을 컨테이너 내부(/app)로 복사
COPY . .

# 애플리케이션 빌드 설정: Gradle Wrapper를 이용해 프로젝트 빌드 (테스트는 CI에서 함께 수행하므로 제외)
RUN ./gradlew clean build -x test

# Expose Port 설정: 80 포트를 컨테이너 외부에 노출한다고 명시
EXPOSE 80

# 환경 변수 설정: 프로젝트 메타데이터 및 JVM 옵션 세팅
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 실행 명령어 설정: 빌드된 jar 파일을 환경변수를 활용해 실행
CMD ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]