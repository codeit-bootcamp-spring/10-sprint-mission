# 🚀 Project Discodeit (Sprint 8)

> **Discodeit**: 채널 기반 실시간 메시징 플랫폼\
> **Sprint 8 주요 성과**:
>> AWS S3 통합\
> > Docker-Compose 환경 표준화\
> > GitHub Actions & ECS를 이용한 CI/CD 파이프라인 구축\

### 🛠️ Tech Stacks

| Category | Tech Stack |
| :--- | :--- |
| **Framework** | <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/> |
| **Infrastructure** | <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/AWS_ECS-FF9900?style=flat-square&logo=amazonecs&logoColor=white"/> <img src="https://img.shields.io/badge/AWS_ECR-FF9900?style=flat-square&logo=amazonecr&logoColor=white"/> <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white"/> |
| **Storage** | <img src="https://img.shields.io/badge/Amazon_S3-569A31?style=flat-square&logo=amazons3&logoColor=white"/> <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white"/> |

---

## 📌 목차

1. [📊 Test Coverage](#-test-coverage)
2. [🤖 사용 방법](#-사용방법)
    - [📥 Repository Clone](#-repository-clone)
    - [💾 AWS S3 설정](#-선택-aws-s3-설정)
    - [🐳 로컬 실행 (Docker Compose)](#-로컬-실행docker-compose)
3. [☁️ CI/CD 배포 (AWS ECS on EC2)](#️-cicd-배포-aws-ecs-on-ec2)
    - [🏗️ 인프라 구성](#️-인프라-구성)
    - [👷 배포 전략 (Workflow)](#-배포-전략-workflow)

---

## 📊 Test Coverage

[![codecov](https://codecov.io/github/Minbro-Kim/10-sprint-mission/branch/sprint8/graph/badge.svg?token=B0WE4JVIIN)](https://codecov.io/github/Minbro-Kim/10-sprint-mission)

## 🤖 사용방법

### 📥 Repository Clone

- 해당 레포지토리(sprint 8) 로컬 저장

---

### 💾 (선택) AWS S3 설정

- s3 저장방식을 사용하는 경우 필수

#### 🪣 S3 Bucket 생성

- 모든 퍼블릭 액세스를 차단한 범용 버킷 생성

#### 👩‍🎤 IAM User 생성(권장)

- Access Key 생성 및 .csv 파일 다운
- IAM User 인라인 권한 정책 연결
    - json 인라인 정책(app 실행에 필요한 최소한의 권한 정의)
      <details>
        <summary>🔐 보안 정책(JSON) 확인하기 (클릭)</summary>

        ```json
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "S3AppAccess",
                    "Effect": "Allow",
                    "Action": [
                        "s3:PutObject",
                        "s3:GetObject",
                        "s3:GetBucketLocation"
                    ],
                    "Resource": [
                        "s3 arn 주소/*",
                        "s3 arn 주소"
                    ]
                }
            ]
        }
        ```
      </details>

#### 🛜 S3 연결 검증

- `AWSS3TestTest`을 실행하여 S3 업로드/다운로드 테스트
- 환경 변수 미지정 시, 실행 X

```bash
RUN_S3_CONNECT_TEST=true ./gradlew test --tests "com.sprint.mission.discodeit.storage.s3.AWSS3TestTest"
```

---

### 🐳 로컬 실행(Docker compose)

- Docker Compose를 사용하여 Spring Boot 애플리케이션과 PostgreSQL DB를 단일 브리지 네트워크 내에서 실행
- 볼륨을 사용하여 데이터 유지

#### 1️⃣ .env 환경 변수 설정

- 프로젝트 루트(root) 위치에 .env 파일 생성 및 하위 내용 입력

  <details>
      <summary>🔐 .env 확인하기 (클릭)</summary>

    ```properties
        # [필수] Storage 설정 (local 또는 s3)
        STORAGE_TYPE=s3
        STORAGE_LOCAL_ROOT_PATH=.discodeit/storage
        
        # [S3 선택 시 필수] AWS 자격 증명
        AWS_S3_ACCESS_KEY=YOUR_ACCESS_KEY
        AWS_S3_SECRET_KEY=YOUR_SECRET_KEY
        AWS_S3_REGION=ap-northeast-2
        AWS_S3_BUCKET=YOUR_BUCKET_NAME
        AWS_S3_PRESIGNED_URL_EXPIRATION=600
        
        # [필수] Database 설정
        POSTGRES_DB=db_name
        POSTGRES_USER=your_name
        POSTGRES_PASSWORD=your_password
        
        # [필수] Spring Boot Profile
        SPRING_PROFILE=prod
    ```

  </details>

#### 2️⃣ 컨테이너 제어 명령어

- 컨테이너 실행

  ```bash
  docker-compose up
  ```
- 컨테이너 중지

  ```bash
  docker-compose down
  ```
- 컨테이너 볼륨 삭제

  ```bash
  docker-compose down -v
  ```

----

## ☁️ CI/CD 배포 (AWS ECS on EC2)

- GitHub Actions를 사용하여 빌드, 테스트, ECR 푸시 및 ECS 서비스 업데이트를 자동화

### 🏗️ 인프라 구성

- **Compute**: AWS ECS (EC2 Launch Type)
- **Registry**: AWS ECR Public
- **Database**: AWS RDS (PostgreSQL)
- **Storage**: AWS S3 (Static Files)
- **Network**: Single EC2 Instance (Bridge Mode)

#### 1️⃣ RDS 및 DB 스키마 설정

- RDS PostgreSQL 생성
- 외부 접속 도구(ex.Datagrip)를 사용하여 `resources/schema.sql`의 DDL을 실행하여 테이블을 미리 생성

#### 2️⃣ discodeit.env 환경 변수 설정

- S3 Bucket에 업로드

  <details>
    <summary>🔐 discodeit.env 확인하기 (클릭)</summary>

   ```properties
      # Spring Configuration
      SPRING_PROFILES_ACTIVE=prod
      
      # Application Configuration
      STORAGE_TYPE=s3
      AWS_S3_ACCESS_KEY=YOUR_ACCESS_KEY
      AWS_S3_SECRET_KEY=YOUR_SECRET_KEY
      AWS_S3_REGION=ap-northeast-2
      AWS_S3_BUCKET=YOUR_BUCKET_NAME
      AWS_S3_PRESIGNED_URL_EXPIRATION=600
      
      # DataSource Configuration
      RDS_ENDPOINT=YOUR_RDS_URI:PORT
      SPRING_DATASOURCE_URL=jdbc:postgresql://${RDS_ENDPOINT}/YOUR_DB_NAME
      SPRING_DATASOURCE_USERNAME=user_name
      SPRING_DATASOURCE_PASSWORD=user_password
      
      # JVM Configuration (프리티어 고려)
      JVM_OPTS=-Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC
   ```
  </details>

#### 3️⃣ ECR 설정

- Public ECR 생성

#### 4️⃣ ECS 인프라 설정 (Console)

#### ECS Cluster 생성

- 인프라 > 원하는 용량:    최소 0, 최대 1

##### ECS Task Definition

- 인프라 요구 사항 > 시작 유형: EC2
- 인프라 요구 사항 > 네트워크 모드:    bridge
- 컨테이너-1 > 컨테이너 세부 정보 > 이미지 URI: ECR 이미지 URI:lastest
- 컨테이너-1 > 포트 매핑 호스트 포트: 80, 컨테이너 포트: 80
- 컨테이너-1 > 환경 변수 - 선택 사항 > 파일에서 추가: S3에 업로드한 discodeit.env 파일 지정

##### ECS Service 생성

- 배포 구성 > 태스크 정의 패밀리: 생성한 Task Definition 선택
- 배포 구성 > 원하는 태스크 1 기본값
- EC2 보안그룹: HTTP 모든 요청 허용

#### 5️⃣ Github Actions 파이프라인 구성

##### 🧑‍🎤 IAM 사용자 생성

- 파이프라인에서 필요한 최소 권한 정책 부여

  <details>
    <summary>🔐 보안 정책(JSON) 확인하기 (클릭)</summary>

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "ECRAuth",
                "Effect": "Allow",
                "Action": [
                    "ecr-public:GetAuthorizationToken",
                    "sts:GetServiceBearerToken"
                ],
                "Resource": "*"
            },
            {
                "Sid": "ECRPublicPush",
                "Effect": "Allow",
                "Action": [
                    "ecr-public:BatchCheckLayerAvailability",
                    "ecr-public:CompleteLayerUpload",
                    "ecr-public:InitiateLayerUpload",
                    "ecr-public:PutImage",
                    "ecr-public:UploadLayerPart"
                ],
                "Resource": "arn:aws:ecr-public::{YOUR_ACCOUT_NUM}:repository/{YOUR_ECR_NAME}"
            },
            {
                "Sid": "ECSForAll",
                "Effect": "Allow",
                "Action": [
                    "ecs:DescribeTaskDefinition",
                    "ecs:ListTasks"
                ],
                "Resource": "*"
            },
            {
                "Sid": "ECSDeployment",
                "Effect": "Allow",
                "Action": [
                    "ecs:RegisterTaskDefinition",
                    "ecs:UpdateService",
                    "ecs:DescribeServices",
                    "ecs:DescribeTasks"
                ],
                "Resource": [
                    "{CLUSTER-ARN}",
                    "{CLUSTER-ARN}/*",
                    "{SERVICE-ARN}",
                    "{TASK-ARN}:*"
                ]
            },
            {
                "Sid": "IAMPassRole",
                "Effect": "Allow",
                "Action": "iam:PassRole",
                "Resource": "arn:aws:iam::{YOUR_ACCOUNT_NUM}:role/ecsTaskExecutionRole",
                "Condition": {
                    "StringLike": {
                        "iam:PassedToService": "ecs-tasks.amazonaws.com"
                    }
                }
            }
        ]
    }
    ```
  </details>

##### 🔑 Github 환경변수 입력

- Github > Repo > Settings > Secrets and Variables > Actions
    - Secrets
        - `AWS_ACCESS_KEY`: IAM 사용자의 액세스 키
        - `AWS_SECRET_KEY`: IAM 사용자의 시크릿 키
    - Variables
        - `AWS_REGION`: AWS 리전(`ap-northeast-2`)
        - `ECR_REPOSITORY_URI`: ECR 레포지토리 URI
        - `ECS_CLUSTER`: ECS 클러스터 이름
        - `ECS_SERVICE`: ECS 서비스 이름
        - `ECS_TASK_DEFINITION`: ECS 태스크 정의 이름

#### 👷 배포 전략 (Workflow)

- test.yml
    - test 코드를 검증하는 파이프라인으로 main에 PR 시, 실행(생략가능)
- deploy.yml
    - release 브랜치로 push될 때 실행(변경가능)

> 기본 브랜치 -pr-> main(test 수행) -merge&pr&merge-> release 권장
