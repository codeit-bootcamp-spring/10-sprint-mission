# 10-sprint-mission

[![codecov](https://codecov.io/gh/CHH01/10-sprint-mission/branch/main/graph/badge.svg)](https://codecov.io/gh/CHH01/10-sprint-mission)

스프링 부트 데모 프로젝트입니다.

## CI/CD 환경

- **CI**: GitHub Actions (`test.yml`) - `main` 브랜치 PR 시 테스트 수행 및 CodeCov 리포팅
- **CD**: GitHub Actions (`deploy.yml`) - `release` 브랜치 푸시 시 AWS ECR 이미지 자동 빌드 및 배포

## 주요 기술 스택

- **Core**: Spring Boot 3.5.10
- **Database**: PostgreSQL (Production), H2 (Test)
- **Monitoring**: Spring Boot Actuator
- **Infrastructure**: AWS ECR(Docker), S3(Storage)

## 시작하기

```bash
./gradlew bootRun
```
