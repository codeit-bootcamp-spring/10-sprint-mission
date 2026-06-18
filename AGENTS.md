# AGENTS.md

# 프로젝트 개요

현재 프로젝트는 Spring Boot 기반 Discodeit 백엔드 API 서버입니다.

백엔드는 다음 역할을 함께 수행합니다.
- REST API 제공
- src/main/resources/static 경로의 정적 프론트엔드 파일 서빙

기술 스택:
- Java 17
- Spring Boot 3.4
- Gradle
- Spring Data JPA
- PostgreSQL
- H2
- MapStruct
- AWS S3 SDK
- Docker
- GitHub Actions

---

# 아키텍처 규칙

다음 계층 구조를 반드시 유지합니다.

Controller -> Service -> Repository -> Entity

규칙:
- Controller는 HTTP 요청/응답 처리만 담당
- 비즈니스 로직은 Service 계층에만 작성
- Repository는 DB 접근만 담당
- Controller/Repository에 비즈니스 로직 작성 금지

Service 구현체 위치:
- service/basic

Swagger/OpenAPI 인터페이스 위치:
- controller/api

---

# DTO 규칙

반드시 DTO를 사용합니다.

규칙:
- JPA Entity를 API 응답으로 직접 반환 금지
- request/data/response DTO 구조 유지
- Entity <-> DTO 변환은 MapStruct 사용
- 기존 API 응답 스펙 최대한 유지

---

# JPA 규칙

중요 사항:
- N+1 문제 주의
- pagination 쿼리에서 fetch join 사용 시 주의
- 불필요한 eager loading 지양
- 명시적인 쿼리 최적화 선호

쿼리 수정 전 반드시 확인:
- 연관관계 영향
- pagination 동작
- 쿼리 개수 변화
- 정렬 비용 증가 여부

---

# DB 규칙

DB:
- 개발/운영: PostgreSQL
- 테스트: H2

스키마:
src/main/resources/schema.sql

주요 테이블:
- users
- channels
- messages
- read_statuses
- binary_contents

주요 관계:
User N - N Channel through ReadStatus

---

# ReadStatus 규칙

ReadStatus는 동일한:
- user
- channel

조합에 대해 중복 생성되면 안 됩니다.

기대 동작:
- 중복 생성 요청 시 409 Conflict 반환

관련 로직 수정 시 반드시 해당 정책 유지.

---

# Storage 규칙

저장소 구현체는 다음 설정값으로 결정됩니다.

discodeit.storage.type

가능 값:
- local
- s3

규칙:
- Storage 추상화 유지
- Service 로직이 특정 저장소 구현(local/s3)에 의존하지 않도록 작성

---

# 테스트 규칙

기존 테스트 구조 유지:
- controller
- integration
- repository
- service/basic
- storage/s3

규칙:
- 테스트를 억지로 disable 하지 않기
- assertion 수정으로 우회하지 않기
- 실제 원인 수정 우선
- 통합 테스트 안정성 고려

테스트 실행 시:
- 가능하면 관련 테스트만 우선 실행
- 필요할 때만 전체 테스트 실행

예시:
./gradlew test --tests ReadStatusApiIntegrationTest

---

# API 규칙

주요 API:
- /api/auth
- /api/users
- /api/channels
- /api/messages
- /api/readStatuses
- /api/binaryContents

규칙:
- 기존 API contract 최대한 유지
- HTTP status semantics 유지
- validation 동작 유지

---

# Docker 규칙

중요:
Dockerfile의 PROJECT_VERSION과 build.gradle version은 반드시 일치해야 함.

Docker 수정 전 확인:
- JAR 파일명
- Gradle version 문자열
- build 결과물 경로

---

# 코딩 스타일

- 메서드명은 명확하게 작성
- 메서드는 가능한 작게 유지
- 거대한 Service 메서드 지양
- 가능하면 immutable DTO 사용
- 생성자 주입 사용

---

# 로깅 규칙

- 로그는 읽기 쉽게 유지
- 한글 인코딩 깨짐 방지
- 운영 코드에 과도한 debug 로그 추가 금지

---

# Git 규칙

다음 경로 수정/커밋 주의:
- build/
- .gradle/
- .idea/
- .logs/
- 생성된 로컬 파일

주의 사항:
- untracked 파일 확인
- 바이너리 파일 실수로 수정하지 않기

---

# Agent 작업 규칙

큰 수정 전 반드시:
1. 문제 원인 설명
2. 수정 계획 설명
3. 최소 범위 수정
4. 관련 테스트 실행
5. 변경 파일 요약

명시적으로 요청받지 않은 경우:
- 대규모 리팩토링 금지
- 불필요한 구조 변경 금지
- API 스펙 변경 금지