#1.Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17

#2.작업 디렉토리 설정
# ex)
# WORKDIR /app
# RUN ./gradlew build
# 결과: /app/gradlew build
WORKDIR /app

# 프로젝트 이름 환경변수
ENV PROJECT_NAME=discodeit

# 프로젝트 버전 환경변수
ENV PROJECT_VERSION=1.2-M8

# JVM 실행 옵션 (기본값: 빈 문자열)
ENV JVM_OPTS=""

#현재 프로젝트의 모든 파일을 컨테이너의 /app 디렉토리로 복사.
COPY . /app

#chmod로 실행권한을 부여 && 빌드할때 테스트 제거하고 jar 파일 생성
#-x test는 이미지 빌드 속도 줄인다.
#테스트 실패로 인해 이미지 생성이 막히는것을 방지
RUN chmod +x ./gradlew && ./gradlew clean build -x test

EXPOSE 80

CMD sh -c "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar --server.port=80"