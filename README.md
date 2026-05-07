# 덕후감

## 프로젝트 소개

- 덕후감은 책 읽는 즐거움을 공유하고, 지식과 감상을 나누는 책 덕후들의 커뮤니티 서비스입니다.
- 외부 API를 활용한 도서 정보 등록, 도서에 대한 리뷰 등록, 댓글, 인기도 집계 기능을 지원합니다.
- 프로젝트 기간 : 2026.04.14 ~ 2026.05.08

---

## 팀원 구성

|                                                                                                                 👑 **Leader**                                                                                                                 |                                                      👥 **Member**                                                      |                                                      👥 **Member**                                                      |                                                      👥 **Member**                                                      |                                                      👥 **Member**                                                      |                                                      👥 **Member**                                                      |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|
|                                                                                            **최현호**<br><sup>[CHH01](https://github.com/CHH01)</sup>                                                                                            |                           **김성경**<br><sup>[conradrado](https://github.com/hyunjae3458)</sup>                            |                               **이다솔**<br><sup>[LeeDyol](https://github.com/LeeDyol)</sup>                               |                              **이승민**<br><sup>[chosi123](https://github.com/chosi123)</sup>                              |                              **황민재**<br><sup>[rorm0819](https://github.com/rorm0819)</sup>                              |                              **신지연**<br><sup>[Nooroong](https://github.com/Nooroong)</sup>                              |
| <img src="https://img.shields.io/badge/Leader-%23F05032?style=for-the-badge&logo=git&logoColor=white" alt="Leader" /> <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> |
|                                                                                                                 사용자 관리 API 구현                                                                                                                 |                                                       대시보드 API 구현                                                       |                                                        리뷰 API 구현                                                        |                                                        도서 API 구현                                                        |                                                        댓글 API 구현                                                        |                                                        알림 API 구현                                                        |

---

## 기술 스택

### 백엔드

<!-- Spring Boot, Spring Data JPA -->
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white" /> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=Spring&logoColor=white" />

### 데이터베이스

<!-- PostgreSQL -->
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=PostgreSQL&logoColor=white" />

### 라이브러리


<img src="https://img.shields.io/badge/QueryDSL-00599C?style=for-the-badge&logo=Gradle&logoColor=white" /> <img src="https://img.shields.io/badge/MapStruct-3178C6?style=for-the-badge&logo=Apache%20Maven&logoColor=white" /> <img src="https://img.shields.io/badge/springdoc--openapi-85EA2D?style=for-the-badge&logo=OpenAPI%20Initiative&logoColor=black" />

### 배포

<!-- AWS -->
<a href="https://aws.amazon.com/ko/free/?trk=78c55dff-53b9-4938-8ed3-d071891360dd&sc_channel=ps&ef_id=CjwKCAjwwdbPBhBgEiwAxBRA4a-cP_jWcrByM3CAsNSEe2MaYyZsUNkUDh53sPz-W6ZHLhU8sFes-BoCDe8QAvD_BwE:G:s&s_kwcid=AL!4422!3!795924513347!e!!g!!aws!23533255624!195473571969&gad_campaignid=23533255624&gbraid=0AAAAADjHtp92d6AQ8Wai4xNXsncKTilC8&gclid=CjwKCAjwwdbPBhBgEiwAxBRA4a-cP_jWcrByM3CAsNSEe2MaYyZsUNkUDh53sPz-W6ZHLhU8sFes-BoCDe8QAvD_BwE">
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white"/>
</a>

### 협업

<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" /> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" /> <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white" /> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white" />

---

## ERD

<img width="2018" height="1382" alt="image" src="https://github.com/user-attachments/assets/ded9f891-6368-4f61-b1cf-eb924d62983b" />


---

## 배포 다이어그램

<img width="796" height="534" alt="image (1)" src="https://github.com/user-attachments/assets/52cf0baa-bc29-47da-bab6-3dfce1dd1ea0" />

---

## 팀원별 구현 기능 상세

### 최현호 

- **사용자 관리 API**
    - 사용자 등록, 수정, 논리 / 물리 삭제, 로그인 기능을 담당하는 API 구현
    - 사용자 삭제 시 상태를 삭제 상태로 변경하는 논리 삭제와 데이터 자체를 삭제하는 물리 삭제로 나누어 구현
- **사용자 검증 로직**
    - 동일 이메일을 통한 중복 등록을 방지하는 검증 로직 구현
    - 비밀번호에 대문자, 특수문자가 포함되도록 하는 검증 로직 구현

### 김성경

- **대시보드 관리 API**
    - 대시보드와 관련 값 집계를 위한 API 구현
- **인기 도서 집계**
    - 기간 별(일간, 주간, 월간, 역대) 인기 도서 순위 구하기
    - 연산은 매일 배치로 수행
    - 도서의 점수는 해당 기간의 리뷰수, 평점을 기준으로 적절히 연산합니다.
- **인기 리뷰 집계**
    - 기간 별(일간, 주간, 월간, 역대) 인기 리뷰 순위 구하기
    - 연산은 매일 배치로 수행
    - 리뷰의 점수는 해당 기간의 좋아요 수, 댓글 수를 기준으로 적절히 연산합니다.
- **파워 유저 집계**
    - 기간 별(일간, 주간, 월간, 역대) 활동 점수에 따른 파워 유저 순위 구하기
    - 연산은 매일 배치로 수행
    - 유저의 활동 점수는 해당 기간에 작성한 리뷰의 인기 점수, 참여한 좋아요 수, 댓글 수를 기준으로 적절히 연산
  
### 이다솔

- **리뷰 관리 API**
    - 리뷰 등록, 수정, 삭제, 목록 조회 API 구현
- **리뷰 검증 로직**
    - 도서 별 1개의 리뷰만 등록할 수 있음
    - 본인이 작성한 리뷰만 수정할 수 있음
- **논리 / 물리 삭제 구현**
    - 데이터 삭제 시 바로 DB에서 삭제되지 않고 삭제되었다는 상태만 표시하는 논리 삭제 구현
    - DB에서 관련 정보와 함께 완전히 삭제하는 물리 삭제 구현 (실제 프론트엔드에서는 사용 X)
- **목록 조회**
    - 시간, 평점을 기준으로 한 리뷰 목록 조회 기능 구현
    - 정확한 페이지네이션을 위해 {이전 페이지의 마지막 요소 생성 시간}을 두 번째 정렬 조건으로 활용

### 이승민

- **도서 관리 API**
    - 도서 등록, 수정, 삭제, 단일/목록 조회를 지원하는 API 작성
- **도서 검증 로직**
    - 도서의 ISBN 값이 중복되지 않고, 수정할수 없도록 구현
    - 계산식을 이용해 ISBN 13을 기준으로, 유효한 ISBN인지 검사하는 로직 구현
- **ISBN으로 책 정보 불러오기**
    - Naver API를 활용해 네이버에 저장된 도서 정보들을 불러올 수 있도록 구현
- **이미지로 ISBN 추출**
    - [OCR Space API](https://ocr.space/OCRAPI)를 활용해 도서의 이미지를 받아 자동으로 ISBN을 추출하는 로직 구현
    - 정규식을 활용해 ISBN 값으로 추정되는 후보를 찾아 추출
- **논리 / 물리 삭제 구현**
  - 데이터 삭제 시 바로 DB에서 삭제되지 않고 삭제되었다는 상태만 표시하는 논리 삭제 구현
  - DB에서 관련 정보와 함께 완전히 삭제하는 물리 삭제 구현 (실제 프론트엔드에서는 사용 X)

### 황민재

- **댓글 관리 API**
  - 댓글의 등록, 수정, 삭제, 목록 조회 API 구현
  - 본인이 작성한 댓글만 수정 가능 
  - `{시간 순 정렬}`, ` 기반 목록 조회 기능 구현
  - 정렬 및 커서 페이지네이션을 적용하여 대용량 데이터에서도 효율적으로 조회할 수 있도록 구현
- **논리 / 물리 삭제 구현**
  - 데이터 삭제 시 바로 DB에서 삭제되지 않고 삭제되었다는 상태만 표시하는 논리 삭제 구현
  - DB에서 관련 정보와 함께 완전히 삭제하는 물리 삭제 구현 (실제 프론트엔드에서는 사용 X)

### 신지연

- **알림 관리 API**
  - 알림의 등록, 수정, 목록 조회, 삭제 로직 구현
  - 정렬 및 커서 페이지네이션을 적용하여 대용량 데이터에서도 효율적으로 조회할 수 있도록 구현
- **알림 생성 로직**
  - 내가 작성한 리뷰에 좋아요 또는 댓글이 달리면 알림이 생성
  - 내가 작성한 리뷰의 인기 순위가 각 기간 별 10위 내에 선정되면 알림이 생성
- **자동 연동 설정 및 배치 자동화**
  - 자동 연동 설정 목록 조회 및 활성화 여부 수정 기능 구현
  - 활성화된 지수를 대상으로 일정 주기마다 지수 데이터 연동을 수행하는 Scheduler 기반 배치 로직 구현
- **알림 자동 삭제 구현**
  - 확인한 알림 중 1주일이 경과된 알림을 자동으로 삭제
  - 삭제는 매일 배치로 수행

---

## 도메인 구조 (도메인 주도 설계)

```text
[domain_name]
├─ batch
│  └─ 물리 삭제 배치 및 스케줄러
├─ controller
│  └─ 클라이언트의 요청을 받고 응답을 반환하는 진입점
├─ dto
│  ├─ request  : 클라이언트로부터 전달받는 데이터 객체
│  └─ response : 클라이언트에게 전달하는 데이터 객체
├─ entity
│  └─ 데이터베이스 테이블과 매핑되는 핵심 비즈니스 모델
├─ event
│  └─ 도메인 간 결합도를 낮추기 위한 이벤트 및 리스너
├─ exception
│  └─ 해당 도메인에서 발생하는 전용 커스텀 예외 클래스
├─ mapper
│  └─ Entity와 DTO 간의 변환을 담당하는 매핑 로직
├─ repository
│  └─ 데이터베이스에 접근하는 인터페이스 및 구현체 (Querydsl 포함)
└─ service
   └─ 비즈니스 로직을 수행하고 트랜잭션을 관리하는 계층

```

---

## 구현 홈페이지

[[바로가기] 덕후감 구현 홈페이지](http://52.79.226.93:8080/)

---

## 팀 Notion

[[바로가기] Spring 백엔드 중급 팀 프로젝트 팀 Notion](https://www.notion.so/0fd4735ec2c982afb93101ac4930f430)

---
