# ============ (1) Builder ============
# 빌드 스테이지
# 도커 빌드 시 사용할  이미지 이름을 변수로 지정
#  - ARG: 빌드 시점에만 사용되는 변수를 선언
#  - alpine: Alpine Linux 기반의 경량 이미지 (일반 리눅스 이미지 ~100MB vs Alpine ~5MB)
#            이미지 크기를 줄여 다운로드 및 배포 속도를 높이기 위해 사용
ARG IMAGE=amazoncorretto:17-alpine

## Amazon Corretto 17 이미지를 베이스 이미지로 사용
#  - FROM: 베이스 이미지를 지정
FROM ${IMAGE} AS builder

# 루트 권한으로 변경 (권한 설정/폴더 생성 작업을 위해)
USER root
## 작업 디렉토리를 설정 (/app)
#  - WORKDIR: 이후 명령어들이 실행될 작업 디렉토리를 설정
WORKDIR /app

## 프로젝트 파일을 컨테이너로 복사. 단, 불필요한 파일은 .dockerignore를 활용해 제외
# Gradle 캐시 디렉토리 경로를 환경 변수로 설정 (빌드 속도 향상)
ENV GRADLE_USER_HOME=/home/gradle/.gradle
# gradle 유저 생성 후 디렉토리 소유자 변경
#  - RUN: 컨테이너 내에서 shell 명령어를 실행
#  - gradle 이라는 이름의 일반 사용자를 생성 (-D는 패스워드 없이 생성)
#  - Gradle 캐시 디렉터리를 미리 생성 (-p는 상위 디렉터리도 함께 생성) - 자동 생성 시 소유자가 root가 됨
#  - gradle 유저에게 소유권 이전
#    - -R -> 지정한 경로 아래의 모든 하위 파일/폴더에도 동일하게 적용
#    - gradle:gradle -> 소유자:그룹
#    - gradle 유저의 홈 디렉터리(Gradle 캐시/설정 저장), 애플리케이션 작업 디렉터리 (소스코드 빌드/결과물 생성)
RUN adduser -D gradle && \
    mkdir -p $GRADLE_USER_HOME && \
    chown -R gradle:gradle /home/gradle /app
# 이후 명령어를 gradle 사용자 권한으로 실행 (컨테이너 내에서 root로 프로세스 진행하지 않음 - 보안)
USER gradle

# === 1단계: 의존성 관련 파일만 먼저 복사 (캐시 활용) ===
#  - Gradle 빌드 설정 파일을 먼저 복사하고 의존성을 다운로드
#  - 이후 소스 코드가 변경되더라도, 빌드 설정이 동일하면 이 레이어까지는 Docker 캐시가 재사용됨
#  - COPY: 호스트(로컬 컴퓨터)의 파일을 컨테이너 내부로 복사
#  - --chown=gradle:gradle -> 복사된 파일의 소유자를 gradle 사용자로 설정

# Gradle Wrapper 스크립트 복사 (빌드 실행에 필요)
#  - 프로젝트에 맞는 Gradle 버전을 자동으로 다운로드하고 실행하는 shell 스크립트
#  - 개발자마다 다른 Gradle 버전을 사용하는 문제를 방지
COPY --chown=gradle:gradle gradlew ./
# gradle 폴더 복사 (wrapper 설정 및 실행 환경)
#  - Gradle Wrapper 설정 파일(gradle-wrapper.properties, wrapper JAR)을 포함하는 디렉터리
COPY --chown=gradle:gradle gradle ./gradle
# Gradle 설정 파일 복사 (빌드 스크립트)
#  - build.gradle: 프로젝트의 빌드 스크립트
#  - settings.gradle: 프로젝트 구조 설정 파일
COPY --chown=gradle:gradle build.gradle settings.gradle ./
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드하여 캐시 활용 (코드 변경 없이 재사용 가능)
#  - ./gradlew dependencies -> build.gradle에 정의된 모든 의존성을 다운로드만 실행 (컴파일/빌드 x)
#    - 실제 빌드는 하지 않고, 의존성만 로컬 캐시(GRADLE_USER_HOME)에 저장
#  - no-daemon:
#    - Gradle 데몬 프로세스를 사용하지 않음 (메모리 절약 위함)
#  - --refresh-dependencies:
#    - 캐시된 의존성이 있더라도 원격 저장소에서 최신 상태를 확인 (SNAPSHOT 의존성을 위한 옵션)
#  - || true:
#    - 명령어가 실패하더라도 빌드를 중단하지 않음 (이후 실제 빌드에서 재시도)
RUN ./gradlew --no-daemon --refresh-dependencies dependencies || true

# === 2단계: 소스코드 복사 (코드 변경 시에만 이후 레이어 재실행) ===
#  - 이후부터는 소스 코드가 변경될 때마다 이 레이어와 이후 레이어가 재실행됨
#  - 위의 의존성 레이어는 build.gradle이 변경되지 않는 한 캐시가 유지됨
# 실제 소스코드 복사 (이 시점 이후 변경 시 빌드 다시 수행됨)
COPY --chown=gradle:gradle src ./src
# 애플리케이션 빌드 (테스트 제외, 속도 향상)
#  - build/libs/deokhugam-x.x.x-SNAPSHOT.jar 파일이 생성됨
#    - --no-parallel: 병렬 빌드를 비활성화 (안정성을 위함)
#    - -x test: 테스트 실행을 건너뜀 (테스트는 CI/CD 파이프라인에서 별도로 수행)
RUN ./gradlew clean build --no-daemon --no-parallel -x test

# ============ (2) Runtime ============
# 실행 스테이지: 빌드 결과 실행에 필요한 최소한의 경량 이미지 사용
FROM ${IMAGE}
# 앱 실행 디렉토리 지정
WORKDIR /app

# 포트를 빌드 인자로 선언 (기본값 8080)
ARG SERVER_PORT=8080
# 런타임에서도 참조 가능하도록 ENV로 전파
ENV SERVER_PORT=${SERVER_PORT}

# 애플리케이션 실행용 non-root 사용자 생성
RUN adduser -D appuser && \
    mkdir -p /app/.discodeit/storage && \
    chown -R appuser:appuser /app
USER appuser

# 빌드 스테이지에서 생성한 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE ${SERVER_PORT}

#   wget -qO- http://localhost:${SERVER_PORT}/actuator/health
#     - wget: HTTP 요청을 보내는 CLI 도구 (alpine 이미지에 기본 포함)
#     - -q: quiet 모드 (진행 상황 출력 안 함)
#     - -O-: 응답 결과를 stdout으로 출력 (파일 저장 안 함)
#     - /actuator/health: Spring Boot Actuator의 헬스체크 엔드포인트
#       응답 예시: {"status":"UP"} (정상) / {"status":"DOWN"} (비정상)
#   || exit 1
#     - wget이 실패하면(HTTP 에러 또는 연결 불가) exit code 1을 반환
#     - exit 0 = healthy, exit 1 = unhealthy
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health || exit 1


# JVM Configuration (프리티어 고려)
# -Xmx256m : 최대 힙 메모리를 256MB로 제한
#  - JVM이 이 이상의 힙 메모리를 할당하지 않으며, 초과 시 OutOfMemoryError가 발생
# -Xms128m : 초기 힙 메모리를 128MB로 설정
#  - JVM 시작 시 OS로부터 128MB를 미리 확보하므로, 런타임 중 힙 확장에 따른 지연을 줄여줌
# -XX:MaxMetaspaceSize=128m : 메타스페이스 최대 크기를 128MB로 제한
#  - 메타스페이스는 클래스 메타데이터(클래스 정의, 메서드 정보 등)를 저장하는 네이티브 메모리 영역
#  - 기본값은 무제한. 제한 이유 - 클래스 로딩 누수를 조기에 감지하거나 메모리 사용량을 통제하려는 목적
# -XX:+UseSerialGC : Serial GC를 사용
#  - 단일 스레드로 GC를 수행하는 가장 단순한 컬렉터
#  - GC 중 모든 애플리케이션 스레드가 멈추는(STW) 단점 존재
#  - GC 스레드 자체의 오버헤드가 거의 없어 메모리가 적고 CPU 코어가 제한된 환경(컨테이너, 임베디드 등)에 적합
ENV JVM_OPTS="-Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

ENV SPRING_PROFILES_ACTIVE=prod

## 애플리케이션 실행 명령어를 설정 - 이때 환경변수로 정의한 프로젝트 정보를 활용
# ENTRYPOINT: 컨테이너가 시작될 때 실행할 명령어를 지정
#  - sh -c -> shell을 열어서 뒤의 문자열을 명령어로 실행
#  - $JVM_OPTS -> 환경 변수가 있으면 치환 (없으면 빈 문자열)
#  - java -jar app.jar -> JVM이 app.jar 안의 main() 메서드를 찾아서 실행
#    - exec -> SIGTERM 관련 문제 때문에 사용 (설명 생략)
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]

# === 설명 ===

# < 멀티 스테이지 빌드 >
# - Docker 이미지를 여러 단계(스테이지)로 나누어 빌드하는 방식
# - 1단계: 빌드 스테이지
#   - 소스를 컴파일하고 JAR 파일을 생성
#   - 소스코드, 빌드 도구(gradle), 외부 라이브러리(의존성) 등 빌드에 필요한 모든 것이 포함됨
# - 2단계: 실행 스테이지
#   - 1단계에서 만든 JAR 파일만 복사해서 실행
#   - 소스코드, 빌드 도구 등이 제외되므로 이미지 크기가 훨씩 작아진다.
# => 이렇게 하면 최종 이미지에는 실행에 필요한 것만 포함되어
#    보안성이 높아지고, 이미지 크기가 줄어들며, 배포 속도가 빨라진다.

# < Docker 레이어 캐싱 전략 >
# - Docker는 Dockerfile의 각 명령어(RUN, COPY등)마다 레이어를 생성
# - 이전 빌드와 동일한 레이어는 캐시에서 재사용하여 빌드 속도를 높임
#   - 따라서 자주 변경되지 않는 것(의존성)을 먼저 복사하고,
#   - 자주 변경되는 것(소스 코드)을 나중에 복사하는 것이 전략
# - 소스 코드만 변경된 경우, 의존성 다운로드 레이어는 캐시에서 재사용함

# https://docker-docs.uclv.cu/engine/reference/builder/
#  - Understand how ARG and FROM interact
