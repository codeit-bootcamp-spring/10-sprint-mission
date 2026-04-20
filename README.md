# 10-sprint-mission

[![codecov](https://codecov.io/github/shong9124/10-sprint-mission/graph/badge.svg?token=OGYKP5TGYR)](https://codecov.io/github/shong9124/10-sprint-mission)

## 프로젝트 소개
Spring Boot 기반의 Discodeit 서버 프로젝트입니다.

## 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- GitHub Actions
- AWS (ECS, ECR, S3)

## 주요 기능
- 사용자 관리
- 채널 및 메시지 기능
- 파일 업로드 및 관리

## 심화 요구사항

### Docker 이미지 최적화
- 멀티 스테이지 빌드 적용
- alpine 기반 runtime 이미지 사용
- 기존: 874MB → 변경 후: 590MB (약 32.5% 감소)

### CI/CD 구축
- GitHub Actions를 통한 CI 구축
- main 브랜치 대상 PR 시 테스트 자동 실행
- JaCoCo + Codecov 연동하여 테스트 커버리지 확인