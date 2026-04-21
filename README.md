Codecov badge  
[![codecov](https://codecov.io/github/raonPsm/10-sprint-mission/branch/sprint8/graph/badge.svg?token=DR8JUG1UJZ)](https://codecov.io/github/raonPsm/10-sprint-mission)

Sunburst  
![Codecov Sunburst](https://codecov.io/github/raonPsm/10-sprint-mission/graphs/sunburst.svg?token=DR8JUG1UJZ)  
Grid  
![Codecov Grid](https://codecov.io/github/raonPsm/10-sprint-mission/graphs/tree.svg?token=DR8JUG1UJZ)  
Icicle  
![Codecov Icicle](https://codecov.io/github/raonPsm/10-sprint-mission/graphs/icicle.svg?token=DR8JUG1UJZ)

# [SB] 스프린트 미션 8

## 🏔️ 프로젝트 마일스톤

- 애플리케이션 컨테이너화
- BinaryContentStorage 고도화 (`AWS S3`)
- AWS를 활용한 배포 (`AWS ECS`,`RDS`)
- CI/CD 파이프라인 구축 (`GitHub Actions`)

## 과금 안내사항

### 미션 진행 중 유의사항

이 미션은 AWS 프리티어 할당량 내에서 충분히 수행 가능하도록 설계되었습니다.
불필요한 비용이 발생하지 않도록 미션 요구사항을 사전에 꼼꼼히 확인해 주시기 바랍니다.

### 미션 종료 후 안내사항

멘토님들의 리뷰가 완료된 후에는 추후에 진행할 실습(미션, 중급 프로젝트 등)을 위해,
이번 미션에서 생성했던 모든 AWS 리소스(EC2, RDS, S3)를 반드시 삭제해 주세요.
리소스를 '중지'하는 것이 아닌 '삭제'해야 추가 비용이 발생하지 않습니다.

## 📝 요구사항

### ✏️ 기본 요구사항

## 애플리케이션 컨테이너화

### Dockerfile 작성

- [x] [Amazon Corretto 17 이미지](https://hub.docker.com/layers/library/amazoncorretto/17/images/sha256-8929bdc3e2be20250ae46b3bc1dc361fcd637cd189ec02174251b0e499a22fad)
  를 베이스 이미지로 사용하세요.
- [x] 작업 디렉토리를 설정하세요. (`/app`)
- [x] 프로젝트 파일을 컨테이너로 복사하세요. 단, 불필요한 파일은`.dockerignore`를 활용해 제외하세요.
- [x] Gradle Wrapper를 사용하여 애플리케이션을 빌드하세요.
    - [x] `80`포트를 노출하도록 설정하세요.
- [x] 프로젝트 정보를 환경 변수로 설정하세요.
    - 실행할 jar 파일의 이름을 추론하는데 활용됩니다.
    - `PROJECT_NAME`: discodeit
    - `PROJECT_VERSION`: 1.2-M8
- [x] JVM 옵션을 환경 변수로 설정하세요.
    - `JVM_OPTS`: 기본값은 빈 문자열로 정의
- [x] 애플리케이션 실행 명령어를 설정하세요. 이때 환경변수로 정의한 프로젝트 정보를 활용하세요.

### 이미지 빌드 및 실행 테스트

- [x] Docker 이미지를 빌드하고 태그(`local`)를 지정하세요.
  <details>
    <summary>사용된 명령어</summary> 
  <img width="1168" height="716" alt="docker_image_build" src="https://github.com/user-attachments/assets/46366841-7350-4aa9-b437-cf4082202821" />  
  <img width="1165" height="166" alt="docker_images_build_check" src="https://github.com/user-attachments/assets/cacc69ea-8f94-45a0-a6f8-5b9b821dc78d" />

  `docker build -t discodeit:local .`
    - `docker build` : Docker 이미지를 빌드하는 명령어
    - `-t discodeit:local` : 빌드할 이미지에 discodeit이라는 이름과 local이라는 태그를 붙임
    - `.` : 현재 디렉터리를 빌드 컨텍스트로 사용하며, 이 안의 Dockerfile을 읽어 이미지를 빌드

  </details>

    - [x] 빌드된 이미지를 활용해서 컨테이너를 실행하고 애플리케이션을 테스트하세요.
        - [x] `prod`프로필로 실행하세요.
            - [x] 데이터베이스는 로컬 환경에서 구동 중인 PostgreSQL 서버를 활용하세요.
                - [x] `http://localhost:8081`로 접속 가능하도록 포트를 매핑하세요.

        <details>
          <summary>사용된 명령어</summary>

      ![docker_run_command](https://github.com/user-attachments/assets/14c1166d-619f-4219-bc42-aa202263c43d)
      <img width="1448" height="86" alt="docker_container_check" src="https://github.com/user-attachments/assets/2ba6d659-4e86-4efe-b68c-249bb25bc997" />

          docker run -d \
          --name discodeit \
          -p 8081:80 \
          -e JVM_OPTS="-Dspring.profiles.active=prod" \
          -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/discodeit" \
          -e SPRING_DATASOURCE_USERNAME="***" \
          -e SPRING_DATASOURCE_PASSWORD="***" \ 
          discodeit:local

        - `docker run` : 이미지로부터 새 컨테이너를 만들고 실행
        - `-d` : detached 모드 - 컨테이너를 백그라운드에서 실행
        - `--name discodeit` : 컨테이너에 discodeit이라는 이름 부여. 없으면 랜덤 이름 생성됨
        - `-p 8081:80` : 호스트 8081 -> 컨테이너 80 포트 매핑
        - `-Dspring.profiles.active=prod` : prod 프로필 활성화
        - `host.docker.internal` : 컨테이너 안에서 호스트의 localhost를 가리키는 특수 DNS.
          로컬 PostgreSQL에 접근하기 위해 localhost 대신 이걸 사용해야 한다.
        </details>

### Docker Compose 구성

- 개발 환경용`docker-compose.yml`파일을 작성합니다.
- [x] 애플리케이션과 PostgreSQL 서비스를 포함하세요.
- [x] 각 서비스에 필요한 모든 환경 변수를 설정하세요.
    - `.env`파일을 활용하되,`.env`는 형상관리에서 제외하여 보안을 유지하세요.
- [x] 애플리케이션 서비스를 로컬 Dockerfile에서 빌드하도록 구성하세요.
- [x] 애플리케이션 볼륨을 구성하여 컨테이너가 재시작되어도`BinaryContentStorage`데이터가 유지되도록 하세요.
- [x] PostgreSQL 볼륨을 구성하여 컨테이너가 재시작되어도 데이터가 유지되도록 하세요.
- [x] PostgreSQL 서비스 실행 후`schema.sql`이 자동으로 실행되도록 구성하세요.
- [x] 서비스 간 의존성을 설정하세요(`depends_on`).
- [x] 필요한 포트 매핑을 구성하세요.
- [x] Docker Compose를 사용하여 서비스를 시작하고 테스트하세요.
    - `--build`플래그를 사용하여 서비스 시작 전에 이미지를 빌드하도록 합니다.
    <details>
    <summary>사용된 명령어</summary> 

   <img width="971" height="731" alt="docker_compose_up_build" src="https://github.com/user-attachments/assets/0c8c3e71-7d66-444d-ab19-50c1048e6d64" />
   <img width="966" height="887" alt="docker_compose_up_build_log" src="https://github.com/user-attachments/assets/a4cbaff2-20d3-40ee-9792-2fcd6711eb42" />

  `docker compose up --build` 명령으로 이미지 빌드 + 컨테이너 실행을 한번에 수행
  `docker compose up` : docker-compose.yml에 정의된 모든 컨테이너를 생성하고 실행
  `--build` : 실행 전에 이미지를 새로 빌드. 이 옵션이 없으면 기존에 빌드된 이미지가 있을 경우 그대로 재사용.
    </details>

## BinaryContentStorage 고도화 (AWS S3)

### AWS S3 버킷 구성

- [x] AWS S3 버킷을 생성하세요.
    - [x] 버킷 이름을`discodeit-binary-content-storage-(사용자 이니셜)`형식으로 지정하세요.
    - [x] 퍼블릭 액세스 차단 설정을 활성화하세요(모든 퍼블릭 액세스 차단).
    - [x] 버전 관리는 비활성화 상태로 두세요.

<details>
  <summary>AWS S3 설정</summary>

![s3_bucket_1](https://github.com/user-attachments/assets/03fee4a5-b55c-4902-9f00-e6eec24a1c5a)

### 일반 구성

### AWS 리전

`ap-northeast-2`

### 버킷 네임스페이스

- 버킷 네임스페이스: 계정 리전 네임스페이스
    - **글로벌 네임스페이스**: 전통적인 방식으로, 버킷 이름이 전 세계 모든 AWS 계정에서 유일해야 한다.
    - **계정 리전 네임스페이스(권장)**: 선택된 옵션. 버킷 이름이 내 계정+리전 내에서만 유일하면 되므로 이름 충돌이 적고, 다른 계정에서는 같은 이름을 쓸 수 있다.

### 버킷 이름 / 버킷 이름 접두사 / 전체 버킷 이름

- 버킷 이름 구성
    - **접두사**: 사용자가 직접 지정하는 부분 (`discodeit-binary-content-storage-raonpsm`)
    - **접미사**: AWS가 자동으로 붙이는 부분 (`-xxx-ap-northeast-2-an`), 계정 ID와 리전 정보가 포함된다.

### 기존 커빗에서 설정 복사 - 선택 사항

이미 있는 버킷의 설정(권한, 버전 관리, 암호화 등)을 그대로 복사해서 적용할 수 있는 선택 옵션
<img width="1013" height="915" alt="s3_bucket_2" src="https://github.com/user-attachments/assets/f9a73968-3135-4b9b-95ad-7f46500af48f" />

### 객체 소유권

버킷에 올라간 객체(파일)를 누가 소유하고, 접근 권한을 어떻게 관리할지 결정

- **ACL 비활성화(권장)** — 현재 선택된 옵션. 모든 객체를 버킷 소유자(내 계정)가 소유하며, 접근 제어는 오직 **정책(Policy)**으로만 관리한다.
- **ACL 활성화** — 다른 AWS 계정이 객체를 소유할 수 있고, ACL(접근 제어 목록)로 개별 객체마다 권한을 따로 설정할 수 있다. 레거시 방식이라 특별한 이유가
  없으면 활성화하지 않는다.

### 이 버킷의 퍼블릭 액세스 차단 설정

버킷과 객체가 인터넷에 공개되는 것을 방지하는 안전장치.

- **새 ACL을 통한 퍼블릭 액세스 차단** — 앞으로 추가되는 퍼블릭 ACL을 차단
- **임의의 ACL을 통한 퍼블릭 액세스 차단** — 기존 포함 모든 퍼블릭 ACL 무시
- **새 버킷/액세스 지점 정책을 통한 퍼블릭 액세스 차단** — 새로 만드는 퍼블릭 정책 차단
- **임의의 버킷/액세스 지점 정책을 통한 퍼블릭 액세스 차단** — 기존 포함 모든 퍼블릭 정책 무시
  웹사이트 호스팅처럼 의도적으로 공개해야 하는 경우가 아니라면 모두 켜둔 채로 유지하는 것이 안전하다.

### 버킷 버전 관리

같은 파일을 덮어쓸 때 이전 버전을 보존할지 여부.

- **비활성화** — 덮어쓰면 이전 파일이 사라진다.
- **활성화** — 모든 버전이 보관되어 실수로 삭제/덮어써도 복원 가능하다. 단, 버전마다 저장 비용이 발생.
  <img width="1016" height="932" alt="s3_bucket_3" src="https://github.com/user-attachments/assets/f8ed455a-b7ce-4045-9118-7bd842c1c1e2" />

### 태그 - 선택사항

버킷에 키-값 쌍의 라벨을 붙이는 기능. 최대 50개까지 추가할 수 있다.

**용도**:

- **비용 추적** — 예: `project: discodeit`, `team: backend` 태그를 붙이면 AWS 비용 탐색기에서 프로젝트/팀별 S3 비용을 분리해서 볼 수
  있다.
- **접근 제어** — IAM 정책에서 특정 태그가 붙은 버킷에만 접근을 허용/차단할 수 있다.
- **자동화/관리** — 태그 기반으로 리소스를 필터링하거나 자동화 스크립트에서 활용할 수 있다.

### 기본 암호화

버킷에 저장되는 모든 객체를 서버 측에서 자동으로 암호화하는 설정.

**암호화 유형** (3가지 중 택 1):

- **SSE-S3** — AWS가 키를 자동으로 생성·관리. 무료이고 가장 간단하다. 대부분의 경우 이것으로 충분하다.
- **SSE-KMS** — AWS KMS(Key Management Service)에서 관리하는 키를 사용한다. 키 사용 로그 추적, 키 회전 정책 등 세밀한 제어가 가능하지만
  KMS API 호출 비용이 발생한다.
- **DSSE-KMS** — KMS 키를 사용하되 **이중 계층** 암호화를 적용한다. 규제가 엄격한 환경(금융, 군사 등)에서 요구될 수 있으며, 가장 강력하지만 비용도 가장
  높다.

**버킷 키**:

- **활성화** — SSE-KMS 사용 시 KMS API 호출 횟수를 줄여 비용을 절감한다. 버킷 수준의 키를 캐싱하는 방식이다. SSE-S3만 쓸 경우에는 큰 의미 없다.

### 고급 설정 - 객체 잠금

WORM(Write-Once-Read-Many) 모델로, 한번 저장된 객체를 일정 기간 또는 영구적으로 삭제·덮어쓰기 불가하게 만드는 기능.

- **비활성화** — 일반적인 사용. 자유롭게 삭제/수정 가능하다.
- **활성화** — 법적 보존 의무(의료기록, 금융 감사 로그 등)가 있을 때 사용한다. 활성화하면 버전 관리도 자동으로 켜진다.

</details>

### AWS S3 접근을 위한 IAM 구성

- [x] S3 버킷에 접근하기 위한 IAM 사용자(`discodeit`)를 생성하세요.
- [x] `AmazonS3FullAccess` 권한을 할당하고, 사용자 생성을 완료하세요.
- [x] 생성된 사용자에 엑세스 키를 생성하세요.
- [x] 발급받은 키를 포함해서 AWS 관련 정보는 `.env` 파일에 추가합니다.
  ```
  ...
  # AWS AWS_S3_ACCESS_KEY=**엑세스_키**
  AWS_S3_SECRET_KEY=**시크릿_키**
  AWS_S3_REGION=**ap-northeast-2**
  AWS_S3_BUCKET=**버킷_이름**
  ```
    - 작성한`.env`파일은 리뷰를 위해 PR에 별도로 첨부해주세요. 단, 엑세스 키와 시크릿 키는 제외하세요.

<details>
<summary>IAM 구성 및 엑세스 키 생성</summary>

<img width="1042" height="473" alt="IAM_1" src="https://github.com/user-attachments/assets/487067eb-7aa3-44c2-9fe5-c961ad579406" />

### 사용자 세부 정보 지정

**AWS Management Console 액세스 권한 제공**:
이 옵션을 켜면 이 IAM 사용자가 웹 콘솔(브라우저)에 로그인할 수 있게 된다.
<img width="1032" height="756" alt="IAM_2" src="https://github.com/user-attachments/assets/c6f94635-2d39-4480-800c-793fba934995" />

### 권한 설정

**권한 옵션** (3가지 중 택 1):

- **그룹에 사용자 추가** — 미리 만들어둔 그룹에 넣어서 그룹의 권한을 상속받는 방식. 여러 사용자를 직무별로 관리할 때 가장 권장된다.
- **권한 복사** — 기존 사용자의 권한을 그대로 복제한다.
- **직접 정책 연결(현재 선택)** — 사용자에게 정책을 바로 붙이는 방식. 간편하지만 사용자가 많아지면 관리가 어려워진다.

`AmazonS3FullAccess` :
**모든 S3 버킷에 대한 모든 작업**(읽기, 쓰기, 삭제, 설정 변경 등)을 허용. 편리하지만 범위가 넓으므로, 운영 환경에서는 특정 버킷만 접근 가능한 커스텀 정책을 만드는
것이 더 안전하다.

**권한 경계 — 선택 사항**:
이 사용자가 가질 수 있는 **최대 권한의 상한선**을 설정하는 기능. 예를 들어 S3FullAccess를 부여하더라도 권한 경계에서 특정 버킷만 허용하면 그 범위로 제한된다.
<img width="1032" height="600" alt="IAM_AmazonS3FullAccess" src="https://github.com/user-attachments/assets/327f1088-c535-4115-bc68-e20200b767b2" />

**사용자 세부 정보**:

- 사용자 이름: `discodeit`
- 콘솔 암호 유형: `None` — 웹 콘솔 로그인 불가 (프로그래밍 전용)
- 암호 재설정 필요: `아니요`

**권한 요약**:

- `AmazonS3FullAccess` (AWS 관리형 정책) — 모든 S3 작업 허용

**태그**:

- 현재 없음. 필요시 추가 가능 (예: `project: discodeit`)

<img width="859" height="855" alt="access_key_1" src="https://github.com/user-attachments/assets/3a899e50-85c4-4fae-a458-dbac592655d2" />
<img width="868" height="319" alt="access_key_2" src="https://github.com/user-attachments/assets/7e198566-d066-474f-8d81-e4132b1164cd" />

**사용 사례** (택 1):

- **Command Line Interface(CLI)** — 터미널에서 `aws` 명령어로 사용
- **로컬 코드** — 내 PC에서 실행하는 애플리케이션(Java, Python 등)에서 SDK로 사용
- **AWS 컴퓨팅 서비스에서 실행되는 애플리케이션** — EC2, Lambda, ECS 등에서 사용. 단, 이 경우 액세스 키 대신 **IAM 역할(Role)**을 사용하는
  것이 권장된다.
- **서드 파티 서비스** — 외부 모니터링/관리 도구에 키를 제공
- **AWS 외부에서 실행되는 애플리케이션(현재 선택)** — AWS 밖의 서버나 데이터센터에서 실행되는 앱에서 사용
- **기타** — 위에 해당하지 않는 경우

**권장되는 대안**:
AWS는 장기 액세스 키 대신 **IAM Roles Anywhere**를 권장한다. 이는 외부 워크로드에 임시 보안 인증을 발급하는 방식으로, 키가 유출되어도 자동 만료되어 더
안전하다.

<img width="864" height="570" alt="access_key_3" src="https://github.com/user-attachments/assets/0dcb1959-913c-455c-8148-ce50fcb335ea" />

</details>

### AWS S3 테스트

- [x] AWS S3 SDK 의존성을 추가하세요.  
  `implementation 'software.amazon.awssdk:s3:2.31.7'`
- [x] S3 API를 간단하게 테스트하세요.
    - 패키지명:`com.sprint.mission.discodeit.stoarge.s3`
    - 클래스명:`AWSS3Test`
        - [x] `Properties`클래스를 활용해서`.env`에 정의한 AWS 정보를 로드하세요.
    - [x] 작업 별 테스트 메소드를 작성하세요.
        - 업로드
        - 다운로드
        - PresignedUrl 생성

### AWS S3를 활용한`BinaryContentStroage`고도화

- [x] 앞서 작성한 테스트 메소드를 참고해 `S3BinaryContentStorage`를 구현하세요.
    - 클래스 다이어그램
      ![fxlj0hb8j-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=13951&version=1&directory=/fxlj0hb8j-image.png&name=fxlj0hb8j-image.png)
- [x] `discodeit.storage.type` 값이 `s3`인 경우에만 Bean으로 등록되어야 합니다.
- [x] `S3BinaryContentStorageTest`를 함께 작성하면서 구현하세요.
- [x] `BinaryContentStorage` 설정을 유연하게 제어할 수 있도록 `application.yaml`을 수정하세요.
  ![[Pasted image 20260406141924.png]]
    - [x] AWS 관련 정보는 형상관리하면 안되므로`.env`파일에 작성된 값을 임포트하는 방식으로 설정하세요.
    - [x] Docker Compose에서도 위 설정을 주입할 수 있도록 수정하세요.
- [x] `download` 메소드는 `PresignedUrl`을 활용해 리다이렉트하는 방식으로 구현하세요.

## AWS를 활용한 배포 (AWS RDS, ECR, ECS)

### AWS RDS 구성

- [x] AWS RDS PostgreSQL 인스턴스를 생성하세요.

  |항목|값|비고|
                          |---|---|---|
  |데이터베이스 생성 방식|표준 생성||
  |엔진 옵션 > 엔진 유형|PostgreSQL||
  |엔진 옵션 > 엔진 버전|17.2-R2|기본값|
  |템플릿|프리 티어|과금 주의|
  |설정 > DB 인스턴스 식별자|discodeit-db||
  |설정 > 자격증명설정 > 마스터 사용자 이름|postgres|기본값|
  |설정 > 자격증명설정 > 자격 증명 관리|자체 관리|기본값|
  |설정 > 자격증명설정 > 마스터 암호|임의의 값|따로 메모해두세요.|
  |인스턴스 구성 > DB 인스턴스 클래스|db.t4g.micro|기본값|
  |연결 > 퍼블릭 액세스|아니오|과금 주의|
  |연결 > 추가구성 > 데이터베이스 포트|5432|기본값|
  |모니터링 > 보존기간|7일 (프리티어)|과금 주의|
  |모니터링 > 추가 모니터링 설정|모두 체크 해제|기본값, 과금 주의|
  |추가 구성 > 백업|체크 해제|과금 주의|
    - 이외 설정은 기본값을 유지하세요.
- [x] 과금이 발생할 수 있으니 다음 항목은 한번 더 확인해주세요.
    - [x] 템플릿:`프리티어`
    - [x] 퍼블릭 액세스:`아니오`
    - [x] 모니터링 > 보존기간:`7일`
    - [x] 모니터링 > 추가 모니터링 설정:`모두 체크 해제`
    - [x] 추가 구성 > 백업:`비활성화`
- [x] SSH 터널링을 통해 개발 환경에서 접근할 수 있도록 EC2를 구성하세요.
    - [x] EC2 인스턴스를 생성하세요.

      |항목|값|비고|
                                                                  |---|---|---|
      |이름 및 태그|rds-ssh||
      |인스턴스 유형|t2.micro|기본값, 과금 주의|
      |키 페어|새 키 페어 생성|.pem 파일 저장 위치를 기억하세요.|
      |네트워크 설정 > 방화벽(보안그룹)|기존 보안 그룹 선택||
        - 이외 설정은 기본값을 유지하세요.
    - [x] 보안 그룹에서 인바운드 규칙을 편집하세요.
        - 유형:`SSH`
        - 소스:`내 IP`
            - 작업 환경의 네트워크(와이파이 등)가 달라지면 계속 수정해주어야 할 수 있습니다.
    - [x] DataGrip을 통해 연결 후 데이터베이스와 사용자, 테이블을 초기화하세요.
        - [x] 데이터 소스 추가 시 `SSH/SSL > Use SSH tunnel` 설정을 활성화하세요. 이때 이전에 다운로드한 `.pem` 파일을 활용하세요.
        - [x] 연결이 성공하면 데이터베이스와 사용자, 테이블을 초기화하세요.

          ```SQL
          -- 1. 새 유저 'discodeit_user' 생성 (비밀번호는 원하는 값으로 설정)
          CREATE USER discodeit_user WITH PASSWORD 'discodeit1234';
          -- 2. postgres 계정은 AWS RDS 환경 특성상 완전한 super user가 아니므로, discodeit_user에 대한 권한을 추가로 부여해야함.
          GRANT discodeit_user TO postgres;
          -- 3. 'discodeit' 데이터베이스 생성 (소유자는 'discodeit_user')
          CREATE DATABASE discodeit OWNER discodeit_user;
          -- 4. schema.sql 실행하여 테이블 생성`
        - [x] 구성이 완료되면 `rds-ssh` 인스턴스는 완전히 삭제하여 과금에 유의하세요.

### AWS ECR 구성

- [x] 이미지를 배포할 퍼블릭 레포지토리(`discodeit`)를 생성하세요.
    - 프라이빗 레포지토리는 용량 제한이 있으므로 퍼블릭 레포지토리로 생성합니다.
- [x] [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html#getting-started-install-instructions)
  를 설치하세요.
- [x] `aws configure` 실행 후 앞서 생성한 `discodeit` IAM 사용자 정보를 입력하세요.
    - 엑세스 키
    - 시크릿 키
    - region:`ap-northeast-2`
    - output format:`json`
- [x] `discodeit` IAM 사용자가 ECR에 접근할 수 있도록 다음 권한을 부여하세요.
    - `AmazonElasticContainerRegistryPublicFullAccess`
- [x] Docker 클라이언트를 배포할 레지스트리에 대해 인증합니다.
    - AWS 콘솔을 통해 생성한 레포지토리 페이지로 이동 후 우측 상단 `푸시 명령 보기`를 클릭하면 관련 명령어를 확인할 수 있습니다.
      ```Bash
      # 예시 
      aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/...`
      ```
- [x] 멀티플랫폼을 지원하도록 애플리케이션 이미지를 빌드하고, `discodeit` 레포지토리에 **push** 하세요.
    - 태그명:`latest`,`1.2-M8`
    - 멀티플랫폼:`linux/amd64`,`linux/arm64`
- [x] AWS 콘솔에서 푸시된 이미지를 확인하세요.

### AWS ECS 구성

- [x] 배포 환경에서 컨테이너 실행 간 사용할 환경 변수를 정의하고, S3에 업로드하세요.
    - [x] `discodeit.env` 파일을 만들어 다음의 내용을 작성하세요.

      ```
      # Spring Configuration
      SPRING_PROFILES_ACTIVE=prod
      
      # Application Configuration
      STORAGE_TYPE=s3
      AWS_S3_ACCESS_KEY=엑세스_키
      AWS_S3_SECRET_KEY=시크릿_키
      AWS_S3_REGION=ap-northeast-2
      AWS_S3_BUCKET=버킷_이름
      AWS_S3_PRESIGNED_URL_EXPIRATION=600
      
      # DataSource Configuration
      RDS_ENDPOINT=RDS_엔드포인트(포트 포함)
      SPRING_DATASOURCE_URL=jdbc:postgresql://${RDS_ENDPOINT}/discodeit
      SPRING_DATASOURCE_USERNAME=RDS_유저네임(DataGrip을 통해 생성했던 유저)
      SPRING_DATASOURCE_PASSWORD=RDS_비밀번호
      
      # JVM Configuration (프리티어 고려)
      JVM_OPTS="-Xmx384m -Xms256m -XX:MaxMetaspaceSize=64m -XX:+UseSerialGC"`
      ```
    - [x] 이 파일을 S3에 업로드하세요.
    - [x] 이 파일은 형상관리되지 않도록 주의하세요.
- [x] AWS ECS 콘솔에서 클러스터를 생성하세요.

  |항목|값|비고|
            |---|---|---|
  |클러스터 구성 > 클러스터 이름|discodeit-cluster||
  |인프라 > AWS Fargate(서버리스)|체크해제|과금 주의|
  |인프라 > Amazon EC2 인스턴스|체크||
  |인프라 > EC2 인스턴스 유형|t2.micro|과금 주의|
  |인프라 > 원하는 용량|최소 0, 최대 1|과금 주의|
  |인프라 > SSH 키 페어|새 키 페어 생성 후 지정||
    - 이외 설정은 기본값을 유지하세요.
- [x] 태스크를 정의하세요.

  |항목|값|비고|
            |---|---|---|
  |태스크 정의 구성 > 태스크 정의 패밀리|discodeit-task||
  |인프라 요구 사항 > 시작 유형|AWS Fargate: 체크 해제, Amazon EC2 인스턴스: 체크||
  |인프라 요구 사항 > 네트워크 모드|bridge||
  |인프라 요구 사항 > 태스크 크기|CPU: 0.25 vCPU, 메모리: 0.5 GB||
  |컨테이너-1 > 컨테이너 세부 정보|이름: discodeit-app, 이미지 URI: 이전에 배포한 이미지||
  |컨테이너-1 > 포트 매핑|호스트 포트: 80, 컨테이너 포트: 80||
  |컨테이너-1 > 리소스 할당 제한 - 조건부|CPU: 0.25 vCPU, 메모리 하드 제한: 0.5 GB, 메모리 소프트 제한: 0.25 GB||
  |컨테이너-1 > 환경 변수 - 선택 사항 > 파일에서 추가|이전에 S3에 업로드한 discodeit.env 파일 지정||
    - 이외 설정은 기본값을 유지하세요.
    - [x] 태스크 생성 후`태스크 실행 역할`에 S3 관련 권한을 추가하세요.
        - 환경 변수 파일을 읽기위해 필요합니다.
- [x] `discodeit` 클러스터 상세 화면에서 서비스를 생성하세요.

  |항목|값|비고|
      |---|---|---|
  |배포 구성 > 태스크 정의 패밀리|discodeit-task||
  |배포 구성 > 서비스 이름|discodeit-service||
  |배포 구성 > 원하는 태스크|1|기본값|
  |배포 구성 > 상태 검사 유예 기간|30초||
    - 이외 설정은 기본값을 유지하세요.
- [x] 태스크의 EC2 보안 그룹의 인바운드 규칙을 설정하여 어디서든 접근할 수 있도록 하세요.
    - [x] EC2 보안 그룹에서 인바운드 규칙을 편집하세요.
    - [x] 규칙 유형으로`HTTP`를 선택하세요.
    - [x] 소스로`Anywhere-IPv4`를 선택하여 모든 IP를 허용하세요.
- [x] 태스크 실행이 완료되면 해당 EC2의 퍼블릭 IP에 접속해보세요.

### ✏️ 심화 요구사항

### 이미지 최적화하기

- [ ] 멀티 스테이지(`빌드`,`런타임`) 빌드를 활용해 이미지의 크기를 줄여보세요.
    - 태그명:`local-slim`
    - 이전에 빌드한 이미지(`1.2-M8`또는`local`)와 크기를 비교해보세요.
- [ ] 이미지 레이어 캐시를 고려해 Dockerfile을 수정해보세요.

### GitHub Actions를 활용한 CI/CD 파이프라인 구축

- [x] **CI**(지속적 통합)를 위한 워크플로우를 설정하세요.
    - [x] `.github/workflows/test.yml` 파일을 생성하세요.
    - [x] `main` 브랜치에 PR이 생성되면 실행되도록 설정하세요.
    - [x] 테스트가 실행하는 Job을 정의하세요.
    - [x] [CodeCov](https://app.codecov.io/)를 통해 테스트 커버리지 뱃지를 README에 추가해보세요.  
      ![tb4cb7hos-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=13953&version=1&directory=/tb4cb7hos-image.png&name=tb4cb7hos-image.png)

- [ ] **CD**(지속적 배포)를 위한 워크플로우를 설정하세요.
    - [x] `.github/workflows/deploy.yml`파일을 생성하세요.
    - [x] `release`브랜치에 코드가 푸시되면 실행되도록 설정하세요.
        - [x] AWS 정보 설정
            - [x] GitHub 레포지토리 설정을 통해 시크릿을 추가하세요.
                - `AWS_ACCESS_KEY`: IAM 사용자의 액세스 키
                - `AWS_SECRET_KEY`: IAM 사용자의 시크릿 키
            - [x] GitHub 레포지토리 설정을 통해 변수를 추가하세요.
                - `AWS_REGION`: AWS 리전(`ap-northeast-2`)
                - `ECR_REPOSITORY_URI`: ECR 레포지토리 URI
                - `ECS_CLUSTER`: ECS 클러스터 이름(`discodeit-cluster`)
                - `ECS_SERVICE`: ECS 서비스 이름(`discodeit-service`)
                - `ECS_TASK_DEFINITION`: ECS 태스크 정의 이름(`discodeit-task`)
        - [ ] Docker 이미지 빌드 및 푸시
            - [ ] Docker 이미지를 빌드하고 푸시하는 Job을 정의하세요.
            - [ ] AWS CLI를 설정하는 Step을 추가하세요.
                - Pubilc ECR에 배포해야하므로 리전은`us-east-1`으로 설정해야합니다.
            - [ ] ECR 로그인 Step을 추가하세요.
                - Public ECR에 로그인해야합니다.
            - [ ] Docker 이미지 빌드 및 푸시하는 과정을 Step으로 추가하세요.
                - 단, 빌드 시간 단축을 위해 멀티 플랫폼 옵션은 제외합니다.
                - GitHub Actions의 런타임 OS와 우리가 배포할 ECS는 모두`x86_64`입니다.
            - [ ] 이미지 태그는`latest`와 GitHub 커밋 해시를 사용하도록 설정하세요.
        - [ ] ECS 서비스 업데이트
            - [ ] ECS 서비스를 업데이트하는 Job을 정의하세요.
            - [ ] AWS CLI를 설정하는 Step을 추가하세요.
                - 우리의 ECS 클러스터에 접근해야하므로 리전은`AWS_REGION`으로 설정해야합니다.
            - [ ] 태스크 정의를 업데이트하는 Step을 추가하세요.
                - 기존의 태스크 정의를 기반으로 새 이미지를 사용하도록 업데이트하세요.
            - [ ] 프리티어 리소스를 고려해 AWS CLI를 사용해 기존에 구동 중인 서비스를 중단하는 Step을 추가하세요.
                - `aws ecs update-service --desired-count`옵션을 활용하세요.
            - [ ] 새로 등록한 태스크 정의를 사용하도록 ECS 서비스를 업데이트하는 Step을 추가하세요.
        - [ ] AWS 콘솔을 통해 새로 등록된 태스크 정의로 배포되었는지 확인하세요.

## 리뷰를 위해 PR에 포함해야할 정보

원활한 리뷰를 위해 PR에 다음과 같은 정보를 포함해주세요.

- [ ]`.env`파일 (AWS 키는 제외)

- [ ] RDS
    - AWS 콘솔 인스턴스 상세 페이지 스크린샷 이미지
    - SSH 터널링을 통해 연결한 DataGrip 스크린샷 이미지
        - 생성한 테이블 목록이 보이도록 캡처해주세요.
- [ ] ECR
    - 푸시된 이미지가 보이는 AWS 콘솔 페이지 스크린샷 이미지
- [ ] ECS
    - 실행 중인 태스크 구성정보가 표시된 AWS 콘솔 페이지 스크린샷 이미지
    - 배포된 EC2 엔드포인트
- [ ] VPC
    - 보안 그룹의 인바운드 규칙을 확인할 수 있는 AWS 콘솔 페이지 스크린샷 이미지
- [ ] IAM
    - 사용자의 권한 정책이 표시된 AWS 콘솔 페이지 스크린샷 이미지

## 🔄 주요 변경사항

## 📸 스크린샷

## 🙇🏽‍♂️ 멘토에게

