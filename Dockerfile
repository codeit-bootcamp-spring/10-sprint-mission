# Amazon Linux 2023 기반의 서버용 JRE
FROM amazoncorretto:17-al2023-headless

# Gradle이 빌드한 파일을 app.jar로 복사
COPY build/libs/*.jar app.jar

# 서비스 노출포트 설정
EXPOSE 80
# sh -c: 쉘을 실행해서 환경변수 읽어오기
#    -l: 리눅스 프로필 설정 읽기
#  exec: 현재 쉘 프로세스를 java 프로세스로 완전히 교체(즉, 쉘은 java를 실행하고 실행종료)
ENTRYPOINT ["sh", "-lc", "exec java $JVM_OPTS -jar app.jar"]