# 베이스 이미지
FROM amazoncorretto:17

# 작업 디렉토리 생성
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""
ENV DB_USERNAME=discodeit_user


# 프로젝트 파일을 컨테이너로 복사
COPY . .

# 어플리케이션 빌드 설정
RUN chmod +x gradlew
RUN ./gradlew build -x test

# 서비스 포트 노출
EXPOSE 80

# 컨테이너가 실행될 때 실행할 명령어
ENTRYPOINT ["sh", "-c", \
"java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]