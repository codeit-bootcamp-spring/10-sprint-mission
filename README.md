### 실행 명령어 정리
Docker Compose 실행
```shell
mkdir -p logs && docker-compose --env-file .env up --build 2>&1 | tee logs/build-$(date +%Y%m%d_%H%M%S).log
```

로컬 실행 (gradlew)
```shell
mkdir -p logs && ./gradlew bootRun 2>&1 | tee logs/build-$(date +%Y%m%d_%H%M%S).log
```

### DB 접속
(Docker) psql 접속
```shell
docker exec -it discodeit-db bash -c 'psql -U $POSTGRES_USER -d $POSTGRES_DB'
```

(로컬) psql 접속
```shell
psql -U discodeit_user -d discodeit -h localhost -p 5432
```

데이터베이스/테이블 조회
```shell
\l              데이터베이스 목록
\c dbname       데이터베이스 전환
\dt             테이블 목록
\dt schema.*    특정 스키마 테이블 목록
\d tablename    테이블 구조 확인
\di             인덱스 목록
\dv             뷰 목록
\ds             시퀀스 목록
\dn             스키마 목록
```

테이블 삭제 명령어
```shell
DROP TABLE IF EXISTS notifications, message_attachments, read_statuses, messages, users, binary_contents, channels CASCADE;
```

# [SB] 스프린트 미션 11

## 🏔️ 프로젝트 마일스톤

- 비동기를 통해 응답 속도 향상하기
- 캐시를 통해 DB I/O 줄이기
- Kafka 도입하기
- Redis 도입하기

### 주요 변경 사항

1. 프로젝트 버전이 변경되었습니다. `v2.2-M11`
- 세부 사항

```markdown
  # build.gradle
  ...
- version = '2.1-M10'
+ version = '2.2-M11'
  ...
```
- `2.2`: `api-doc` 버전을 따릅니다.
- `M11`: 미션 11을 의미합니다.

2. 프론트엔드가 변경되었습니다.
- `v2.2.0`
   - 알림 기능이 추가되었습니다.
   - 파일 업로드 상태 폴링 기능이 추가되었습니다.
   - 채널 별 알림 설정 기능이 추가되  었습니다.
   - 정적 리소스: 베이스 코드에 적용되어 있습니다.
   - 소스 코드 (참고용)
> 프론트엔드 소스 코드는 참고용으로만 활용하세요. 수정하여 활용하는 경우 이어지는 요구사항 또는 미션을 수행하는 데 어려움이 있을 수 있습니다.

## 📝 요구사항

### ✏️ 기본 요구사항

### **Spring Event - 파일 업로드 로직 분리하기**

- 디스코드잇은 BinaryContent의 메타 데이터(DB)와 바이너리 데이터(FileSystem/S3)를 분리해 저장합니다.

- 만약 지금처럼 두 로직이 하나의 트랜잭션으로 묶인 경우 트랜잭션을 과도하게 오래 점유할 수 있는 문제가 있습니다.

    - 바이너리 데이터 저장 연산은 오래 걸릴 수 있는 연산이며, 해당 연산이 끝날 때까지 트랜잭션이 대기해야합니다.
- 따라서 Spring Event를 활용해 메타 데이터 저장 트랜잭션으로부터 바이너리 데이터 저장 로직을 분리하여, 메타데이터 저장 트랜잭션이 종료되면 바이너리 데이터를 저장하도록 변경합니다.

- [ ] `BinaryContentStorage.put`을 직접 호출하는 대신 `BinaryContentCreatedEvent`를 발행하세요.

    - `BinaryContentCreatedEvent`를 정의하세요.
        - `BinaryContent` 메타 정보가 DB에 잘 저장되었다는 사실을 의미하는 이벤트입니다.
    - 다음의 메소드에서 `BinaryContentStorage`를 호출하는 대신 `BinaryContentCreatedEvent`를 발행하세요.
        - `UserService.create`/`update`
        - `MessageService.create`
        - `BinaryContentService.create`
    - `ApplicationEventPublisher`를 활용하세요.
- [ ] 이벤트를 받아 실제 바이너리 데이터를 저장하는 리스너를 구현하세요.

    - 이벤트를 발행한 **메인 서비스의 트랜잭션이 커밋**되었을 때 리스너가 실행되도록 설정하세요.
    - `BinaryContentStorage`를 통해 바이너리 데이터를 저장하세요.
- [ ] 바이너리 데이터 저장 성공 여부를 알 수 있도록 메타 데이터를 리팩토링하세요.

  ![w6750l1ce-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14493&version=1&directory=/w6750l1ce-image.png&name=w6750l1ce-image.png)

    - `BinaryContent`에 바이너리 데이터 업로드 상태 속성(`status`)을 추가하세요.

      ![ef9unz1z7-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14493&version=1&directory=/ef9unz1z7-image.png&name=ef9unz1z7-image.png)

        - `PROCESSING`: 업로드 중
            - 기본값입니다.
        - `SUCCESS`: 업로드 완료
        - `FAIL`: 업로드 실패

      ```sql
      -- schema.sql
      CREATE TABLE binary_contents (
          id           uuid PRIMARY KEY,
          created_at   timestamp with time zone NOT NULL,
          updated_at   timestamp with time zone,
          file_name    varchar(255)             NOT NULL,
          size         bigint                   NOT NULL,
          content_type varchar(100)             NOT NULL,
          status       varchar(20)              NOT NULL
      );

      -- ALTER TABLE binary_contents
      --     ADD COLUMN updated_at timestamp with time zone;
      -- ALTER TABLE binary_contents
      --     ADD COLUMN status varchar(20) NOT NULL;
      ```

    - `BinaryContent`의 상태를 업데이트하는 메소드를 정의하세요.

      ![3o66puyre-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14493&version=1&directory=/3o66puyre-image.png&name=3o66puyre-image.png)

        - 트랜잭션 전파 범위에 유의하세요.
- [ ] 바이너리 데이터 저장 성공 여부를 메타 데이터에 반영하세요.

    - 성공 시 `BinaryContent`의 `status`를 `SUCCESS`로 업데이트하세요.
    - 실패 시 `BinaryContent`의 `status`를 `FAIL`로 업데이트하세요.

### **Spring Event - 알림 기능 추가하기**

- `1)채널에 새로운 메시지가 등록`되거나 `2)권한이 변경된 경우` 이벤트를 발행해 알림을 받을 수 있도록 구현합니다.
- [ ] 채널에 새로운 메시지가 등록된 경우 알림을 받을 수 있도록 리팩토링하세요.
    - `MessageCreatedEvent`를 정의하고 새로운 메시지가 등록되면 이벤트를 발행하세요.

    - 사용자 별로 관심있는 채널의 알림만 받을 수 있도록 **ReadStatus** 엔티티에 채널 알림 여부 속성(`notificationEnabled`)을 추가하세요.

      ![dw0oq8qt4-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/dw0oq8qt4-image.png&name=dw0oq8qt4-image.png)

        - **PRIVATE** 채널은 알림 여부를 `true`로 초기화합니다.
        - **PUBLIC** 채널은 알림 여부를 `false`로 초기화합니다.

      ```sql
      -- schema.sql
      CREATE TABLE read_statuses (
          id                   uuid PRIMARY KEY,
          created_at           timestamp with time zone NOT NULL,
          updated_at           timestamp with time zone,
          user_id              uuid                     NOT NULL,
          channel_id           uuid                     NOT NULL,
          last_read_at         timestamp with time zone NOT NULL,
          notification_enabled boolean                  NOT NULL,
          UNIQUE (user_id, channel_id)
      );

      -- ALTER TABLE read_statuses
      --     ADD COLUMN notification_enabled boolean NOT NULL;
      ```

    - 알림 여부를 수정할 수 있게 `ReadStatusUpdateRequest`를 수정하세요.

      ![cy6bs1tea-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/cy6bs1tea-image.png&name=cy6bs1tea-image.png)

        - 알림이 활성화 되어 있는 경우

          ![qf5fuge9j-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/qf5fuge9j-image.png&name=qf5fuge9j-image.png)

        - 알림이 활성화 되어 있지 않은 경우

          ![ylp4yrq39-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/ylp4yrq39-image.png&name=ylp4yrq39-image.png)


- [ ] 사용자의 권한(Role)이 변경된 경우 알림을 받을 수 있도록 리팩토링하세요.
    - `RoleUpdatedEvent`를 정의하고 권한이 변경되면 이벤트를 발행하세요.
- [ ] 알림 API를 구현하세요.
    - `NotificationDto`를 정의하세요.

      ![42xhee8ts-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/42xhee8ts-image.png&name=42xhee8ts-image.png)

        - `receiverId`: 알림을 수신할 User의 `id`입니다.
    - 알림 조회

        - 엔드포인트: `GET /api/notifications`
        - 요청
            - 헤더: 엑세스 토큰
        - 응답
            - `200 List<NotifcationDto>`
            - `401 ErrorResponse`
    - 알림 확인

        - 엔드포인트: `DELETE /api/notifications/{notificationId}`
        - 요청
            - 헤더: 엑세스 토큰
        - 응답
            - `204 Void`
            - 인증되지 않은 요청: `401 ErrorResponse`
            - 인가되지 않은 요청: `403 ErrorResponse`
                - 요청자 본인의 알림에 대해서만 수행할 수 있습니다.
            - 알림이 없는 경우: `404 ErrorResponse`
- [ ] 알림이 필요한 이벤트가 발행되었을 때 알림을 생성하세요.
    - 이벤트를 처리할 리스너를 구현하세요.

      ```java
      public class NotificationRequiredEventListener {

          @TransactionalEventListener
          public void on(MessageCreatedEvent event) { ... }

          @TransactionalEventListener
          public void on(RoleUpdatedEvent event) { ... }
      }
      ```

        - `on(MessageCreatedEvent)`
            - 해당 채널의 알림 여부를 활성화한 `ReadStatus`를 조회합니다.

            - 해당 `ReadStatus`의 사용자들에게 알림을 생성합니다.

              ```yaml
              # 알림 예시
              title: "보낸 사람 (#채널명)"
              content: "메시지 내용"
              ```

              ![7a6dx6rok-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/7a6dx6rok-image.png&name=7a6dx6rok-image.png)

            - 단, 해당 메시지를 보낸 사람은 알림 대상에서 제외합니다.

        - `on(RoleUpdatedEvent)`
            - 권한이 변경된 당사자에게 알림을 생성합니다.

              ```yaml
              # 알림 예시
              title: "권한이 변경되었습니다."
              content: "USER -> CHANNEL_MANAGER"
              ```

              ![d8epgul6e-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14494&version=1&directory=/d8epgul6e-image.png&name=d8epgul6e-image.png)

### 비동기 적용하기

- [ ] 비동기를 적용하기 위한 설정(`AsyncConfig`) 클래스를 구현하세요.
    - `@EnableAsync` 어노테이션을 활용하세요.
    - `TaskExecutor`를 Bean으로 등록하세요.
    - `TaskDecorator`를 활용해 MDC의 Request ID, SecurityContext의 인정 정보가 비동기 스레드에서도 유지되도록 구현하세요.
- [ ] 앞서 구현한 Event Listener를 비동기적으로 처리하세요.
    - `@Async` 어노테이션을 활용하세요.
- [ ] 동기 처리와 비동기 처리 간 성능 차이를 비교해보세요.
    - 파일 업로드 로직에 의도적인 지연(`Thread.sleep(…)`)을 발생시키세요.

      ```java
      // LocalBinaryContentStorage
      public UUID put(UUID binaryContentId, byte[] bytes) {
          try {
              Thread.sleep(3000);
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new RuntimeException("Thread interrupted while simulating delay", e);
          }
          ...
      }
      ```

    - 메시지 생성 API의 실행 시간을 측정해보세요.

        - `@Timed` 어노테이션을 메소드에 추가합니다.

          ```java
          // MessageController
          @Timed("message.create.async")
          @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
          public ResponseEntity<MessageDto> create(...) { ... }
          ```

        - Actuator 설정을 추가합니다.

          ```yaml
          # application.yaml
          management:
              ...
            observations:
              annotations:
                enabled: true
          ```

        - `/actuator/metrics/message.create.async` 에서 측정된 시간을 확인할 수 있습니다.

    - `@EnableAsync`를 활성화 / 비활성화 해보면서 동기 / 비동기 처리 간 응답 속도의 차이를 확인해보세요.

**statistic 의미 표**

| statistic | 의미 |
|---|---|
| COUNT | 호출 횟수 |
| TOTAL_TIME | 모든 호출의 누적 시간 합계 |
| MAX | 단일 호출 중 가장 오래 걸린 시간 |

**수치 비교 표**

| | 비동기 | 동기 |
|---|---|---|
| COUNT | 8회 | 8회 |
| TOTAL_TIME | 0.165초 | 24.221초 |
| MAX | 0.057초 (57ms) | 3.049초 (3,049ms) |
| 호출당 평균 | 0.165 / 8 = ~20ms | 24.221 / 8 = ~3,028ms |

비동기 처리가 호출당 평균 약 **150배** 빠른 결과를 보여주고 있습니다.
```json
{
  "name": "message.create.async",
  "description": "바이너리 컨텐츠 IO 작업 동기 처리 비동기 처리 간 성능 차이 측정",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 8
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 0.164658874
    },
    {
      "statistic": "MAX",
      "value": 0.056618375
    }
  ],
  "availableTags": [
    {
      "tag": "exception",
      "values": [
        "none"
      ]
    },
    {
      "tag": "method",
      "values": [
        "create"
      ]
    },
    {
      "tag": "class",
      "values": [
        "com.sprint.mission.discodeit.controller.MessageController"
      ]
    }
  ]
}
```

```json
{
  "name": "message.create.async",
  "description": "바이너리 컨텐츠 IO 작업 동기 처리 비동기 처리 간 성능 차이 측정",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 8
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 24.22070125
    },
    {
      "statistic": "MAX",
      "value": 3.04876075
    }
  ],
  "availableTags": [
    {
      "tag": "exception",
      "values": [
        "none"
      ]
    },
    {
      "tag": "method",
      "values": [
        "create"
      ]
    },
    {
      "tag": "class",
      "values": [
        "com.sprint.mission.discodeit.controller.MessageController"
      ]
    }
  ]
}
```

- MDC값이 전파된 것을 확인
```markdown
26-06-03 17:09:37.008 [http-nio-8080-exec-5] DEBUG c.s.m.d.config.MDCLoggingInterceptor [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - Request started  
26-06-03 17:09:37.046 [http-nio-8080-exec-5] INFO  c.s.m.d.controller.MessageController [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - 메시지 생성 요청: request=MessageCreateRequest[content=배, channelId=f625e3da-7879-4f3e-a784-b39ecc9507e8, authorId=19c140e8-99c7-4ac1-ba21-991dbf997f83], attachmentCount=1  
26-06-03 17:09:37.051 [http-nio-8080-exec-5] DEBUG c.s.m.d.s.basic.BasicMessageService  [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - 메시지 생성 시작: request=MessageCreateRequest[content=배, channelId=f625e3da-7879-4f3e-a784-b39ecc9507e8, authorId=19c140e8-99c7-4ac1-ba21-991dbf997f83]  
26-06-03 17:09:37.067 [http-nio-8080-exec-5] INFO  c.s.m.d.s.basic.BasicMessageService  [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - 메시지 생성 완료: id=b4b865a1-6f39-49ab-a19d-95ae34425765, channelId=f625e3da-7879-4f3e-a784-b39ecc9507e8  
26-06-03 17:09:37.080 [http-nio-8080-exec-5] DEBUG c.s.m.d.controller.MessageController [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - 메시지 생성 응답: MessageDto[id=b4b865a1-6f39-49ab-a19d-95ae34425765, createdAt=2026-06-03T08:09:37.066435Z, updatedAt=2026-06-03T08:09:37.066435Z, content=배, channelId=f625e3da-7879-4f3e-a784-b39ecc9507e8, author=UserDto[id=19c140e8-99c7-4ac1-ba21-991dbf997f83, username=admin, email=admin@gmail.com, profile=null, online=true, role=ADMIN], attachments=[BinaryContentDto[id=8d9deebf-49f9-4c16-8659-98bed4562df2, fileName=pear.jpg, size=25090, contentType=image/jpeg, status=PROCESSING]]]  
26-06-03 17:09:37.084 [http-nio-8080-exec-5] DEBUG c.s.m.d.config.MDCLoggingInterceptor [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - Request completed

...

26-06-03 17:09:40.086 [event-1] INFO  c.s.m.d.e.BinaryContentEventListener [021d064cf11a4c90a5b2625829b80f10 | POST | /api/messages] - 바이너리 컨텐츠 파일 저장 성공: id=8d9deebf-49f9-4c16-8659-98bed4562df2
```

### 비동기 실패 처리하기

- 비동기로 처리하는 로직이 실패하는 경우 사용자에게 즉각적인 에러 전파가 되지 않을 가능성이 높습니다.
- 따라서 비동기로 처리하는 로직은 자동 재시도 전략을 통해 더 견고하게 구현해야 합니다.
- 또, 실패하더라도 그 사실을 명확하게 기록해두어야 에러에 대응할 수 있습니다.
- [ ] S3를 활용해 바이너리 데이터 저장 시 자동 재시도 매커니즘을 구축하세요.
    - Spring Retry를 위한 환경을 구성하세요.
        - `org.springframework.retry:spring-retry` 의존성을 추가하세요.
        - `@EnableRetry` 어노테이션을 활용해 Spring Retry를 활성화 하세요.
    - 바이너리 데이터를 저장하는 메소드에 `@Retryable` 어노테이션을 사용해 재시도 정책(횟수, 대기 시간 등)을 설정하세요.
- [ ] 재시도가 모두 실패했을 때 대응 전략을 구축하세요.
    - `@Recover` 어노테이션을 활용하세요.

    - 실패 정보를 관리자에게 통지하세요.

      ![1ubhvrs9g-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14496&version=1&directory=/1ubhvrs9g-image.png&name=1ubhvrs9g-image.png)

      ```text
      # 알림 내용 예시
      RequestId: 7641467e369e458a98033558a83321fb
      BinaryContentId: b0549c2a-014c-4761-8b21-4b77d3bd011c
      Error: The AWS Access Key Id you provided does not exist in our records. (Service: S3, Status Code: 403, Request ID: B7KCVSRCGPYJZREX, Extended Request ID: AWRVuJJJ3upwwOkCnd+yhHkgSajUxdg7L4195lbMVTIka6WnBpjZLLRTReoHbgIMf9zzH/QQM0Y5ZOVJCHF2F+l2mSyPG/+8Ee2XBS8hcqk=) (SDK Attempt Count: 1)
      ```

        - 실패 정보에는 추후 디버깅을 위해 필요한 정보를 포함하세요.
            - 실패한 작업 이름
            - MDC의 Request ID
            - 실패 이유 (예외 메시지)

### 캐시 적용하기

- [ ] Caffeine 캐시를 위한 환경을 구성하세요.
    - `org.springframework.boot:spring-boot-starter-cache` 의존성을 추가하세요.
    - `com.github.ben-manes.caffeine:caffeine` 의존성을 추가하세요.
    - application.yaml 설정 또는 Bean을 통해 캐시 Caffeine 캐시를 설정하세요.
      - [ ] `@Cacheable` 어노테이션을 활용해 캐시가 필요한 메소드에 적용하세요.
    - 사용자별 채널 목록 조회
    - 사용자별 알림 목록 조회
    - 사용자 목록 조회
- [ ] 데이터 변경 시, 캐시를 갱신 또는 무효화하는 로직을 구현하세요.
    - `@CacheEvict`, `@CachePut`, `CacheManager` 등을 활용하세요.
    - 예시:
        - 새로운 채널 추가/수정/삭제 -> 채널 목록 캐시 무효화
        - 알림 추가/삭제 -> 알림 목록 캐시 무효화
        - 사용자 추가/로그인/로그아웃 -> 사용자 목록 캐시 무효화
- [ ] 캐시 적용 전후의 차이를 비교해보세요.
    - 로그를 통해 SQL 실행 여부를 확인해보세요.

캐시 적용됨
```markdown
26-06-04 13:43:02.353 [http-nio-8080-exec-4] DEBUG c.s.m.d.config.MDCLoggingInterceptor [90398105c3ff4c97b8d829e326ea5b58 | GET | /api/users] - Request started
26-06-04 13:43:02.354 [http-nio-8080-exec-4] DEBUG c.s.m.d.config.MDCLoggingInterceptor [90398105c3ff4c97b8d829e326ea5b58 | GET | /api/users] - Request completed
```

캐시 적용되지 않음
```markdown
26-06-04 14:25:34.133 [http-nio-8080-exec-5] INFO  c.s.m.d.controller.UserController    [4a91800d1b0340919c1e66333ee902d6 | PATCH | /api/users/bc77d311-0407-499b-b5b1-2196fe3ef90e] - 사용자 수정 요청: id=bc77d311-0407-499b-b5b1-2196fe3ef90e, request=UserUpdateRequest[newUsername=apple-new, newEmail=null, newPassword=null]
26-06-04 14:25:34.140 [http-nio-8080-exec-5] DEBUG c.s.m.d.s.basic.BasicUserService     [4a91800d1b0340919c1e66333ee902d6 | PATCH | /api/users/bc77d311-0407-499b-b5b1-2196fe3ef90e] - 사용자 수정 시작: id=bc77d311-0407-499b-b5b1-2196fe3ef90e, request=UserUpdateRequest[newUsername=apple-new, newEmail=null, newPassword=null]

...

26-06-04 14:25:34.150 [http-nio-8080-exec-5] INFO  c.s.m.d.s.basic.BasicUserService     [4a91800d1b0340919c1e66333ee902d6 | PATCH | /api/users/bc77d311-0407-499b-b5b1-2196fe3ef90e] - 사용자 수정 완료: id=bc77d311-0407-499b-b5b1-2196fe3ef90e

...

26-06-04 14:25:49.141 [http-nio-8080-exec-2] DEBUG c.s.m.d.config.MDCLoggingInterceptor [5d5bdfd7467d4b278cbeafd15a0c06fa | GET | /api/users] - Request started
26-06-04 14:25:49.142 [http-nio-8080-exec-2] DEBUG c.s.m.d.s.basic.BasicUserService     [5d5bdfd7467d4b278cbeafd15a0c06fa | GET | /api/users] - 모든 사용자 조회 시작
26-06-04 14:25:49.142 [http-nio-8080-exec-2] DEBUG org.hibernate.SQL                    [5d5bdfd7467d4b278cbeafd15a0c06fa | GET | /api/users] - 
    select
        u1_0.id,
        u1_0.created_at,
        u1_0.email,
        u1_0.password,
        p1_0.id,
        p1_0.content_type,
        p1_0.created_at,
        p1_0.file_name,
        p1_0.size,
        p1_0.status,
        p1_0.updated_at,
        u1_0.role,
        u1_0.updated_at,
        u1_0.username 
    from
        users u1_0 
    left join
        binary_contents p1_0 
            on p1_0.id=u1_0.profile_id
26-06-04 14:25:49.144 [http-nio-8080-exec-2] INFO  c.s.m.d.s.basic.BasicUserService     [5d5bdfd7467d4b278cbeafd15a0c06fa | GET | /api/users] - 모든 사용자 조회 완료: 총 3명
26-06-04 14:25:49.146 [http-nio-8080-exec-2] DEBUG c.s.m.d.config.MDCLoggingInterceptor [5d5bdfd7467d4b278cbeafd15a0c06fa | GET | /api/users] - Request completed
```

- [ ] Spring Actuator를 활용해 캐시 관련 통계 지표를 확인해보세요.
    - Caffein Spec에 `recordStats` 옵션을 추가하세요.

      ```yaml
      # application.yaml
      cache:
          ...
        caffeine:
          spec: >
            maximumSize=100,
            expireAfterAccess=600s,
            recordStats
      ```

    - `/actuator/caches`, `/actuator/metrics/cache.*` 를 통해 캐시 관련 데이터를 확인해보세요.

등록된 캐시 목록 확인 http://localhost:8080/actuator/caches
```json
{
  "cacheManagers": {
    "cacheManager": {
      "caches": {
        "userChannels": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache$BoundedLocalManualCache"
        },
        "users": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache$BoundedLocalManualCache"
        },
        "userNotifications": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache$BoundedLocalManualCache"
        }
      }
    }
  }
}
```
캐시 조회 횟수 (hit/miss 합)
http://localhost:8080/actuator/metrics/cache.gets?tag=name:users
```json
{
  "name": "cache.gets",
  "description": "The number of times cache lookup methods have returned a cached (hit) or uncached (newly loaded or null) value (miss).",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 9
    }
  ],
  "availableTags": [
    {
      "tag": "result",
      "values": [
        "hit",
        "miss"
      ]
    },
    {
      "tag": "cache.manager",
      "values": [
        "cacheManager"
      ]
    },
    {
      "tag": "cache",
      "values": [
        "users"
      ]
    }
  ]
}
```
적중(hit)만
http://localhost:8080/actuator/metrics/cache.gets?tag=name:users&tag=result:hit
실패(miss)만
http://localhost:8080/actuator/metrics/cache.gets?tag=name:users&tag=result:miss
evict 횟수 (제거된 항목 수)
http://localhost:8080/actuator/metrics/cache.evictions?tag=name:users

### ✏️ 심화 요구사항
### 유의사항

- 이번 실습은 분산 환경을 대비한 기술 도입을 목표로 합니다. 특히, 여러 서버나 인스턴스로 구성되는 시스템에서 발생할 수 있는 문제를 해결하는 데 필요한 기술을 학습합니다.
    - Kafka를 통한 이벤트 발행/구독
    - Redis를 활용한 전역 캐시 저장소 구성
- 이번 미션에서는 Kafka, Redis의 세부 설정이나 고급 기능보다는, 기술을 간단하게 적용하고, **분산 환경의 필요성과 기존 시스템의 한계**를 이해하는 데 집중해주세요.


### Spring Kafka 도입하기

- 회원이 늘어나면서 알림 연산량이 급증해 알림 기능만 별도의 마이크로 서비스로 분리하기로 결정했다고 가정해봅시다.
- 이제 알림 서비스와 메인 서비스는 완전히 분리된 서버이므로 Spring Event만을 통해서 이벤트를 발행/소비할 수 없습니다.
- 따라서 메인 서비스에서 Kafka를 통해 서버 외부로 이벤트를 발행하고, 알림 서비스에서는 서버 외부의 이벤트를 소비할 수 있도록 해야합니다.
  - [ ] **Kafka 환경을 구성하세요.**
    - Docker Compose를 활용해 Kafka를 구동하세요.

      ```yaml
      # docker-compose-kafka.yaml
      # https://developer.confluent.io/confluent-tutorials/kafka-on-docker/#the-docker-compose-file
      services:
        broker:
          image: apache/kafka:latest
          hostname: broker
          container_name: broker
          ports:
            - 9092:9092
          environment:
            KAFKA_BROKER_ID: 1
            KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT,CONTROLLER:PLAINTEXT
            KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
            KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
            KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
            KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
            KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
            KAFKA_PROCESS_ROLES: broker,controller
            KAFKA_NODE_ID: 1
            KAFKA_CONTROLLER_QUORUM_VOTERS: 1@broker:29093
            KAFKA_LISTENERS: PLAINTEXT://broker:29092,CONTROLLER://broker:29093,PLAINTEXT_HOST://0.0.0.0:9092
            KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
            KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
            KAFKA_LOG_DIRS: /tmp/kraft-combined-logs
            CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
      ```

      ```shell
      docker compose -f docker-compose-kafka.yaml up -d
      ```

    - Spring Kafka 의존성을 추가하고, `application.yml`에 Kafka 설정을 추가하세요.

      ```groovy
      implementation 'org.springframework.kafka:spring-kafka'
      ```

      ```yaml
      # application.yaml
      spring:
          ...
        kafka:
          bootstrap-servers: localhost:9092
          producer:
            key-serializer: org.apache.kafka.common.serialization.StringSerializer
            value-serializer: org.apache.kafka.common.serialization.StringSerializer
          consumer:
            group-id: discodeit-group
            auto-offset-reset: earliest
            key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
            value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      ```

    - [ ] Spring Event를 Kafka로 발행하는 리스너를 구현하세요.

        - `NotificationRequiredEventListener`는 비활성화하세요.

        - `KafkaProduceRequiredEventListener`를 구현하세요.

          ```java
          package com.sprint.mission.discodeit.event.kafka;

          @Slf4j
          @RequiredArgsConstructor
          @Component
          public class KafkaProduceRequiredEventListener {

              private final KafkaTemplate<String, String> kafkaTemplate;
              private final ObjectMapper objectMapper;

              @Async("eventTaskExecutor")
              @TransactionalEventListener
              public void on(MessageCreatedEvent event) {
                  ...
                  String payload = objectMapper.writeValueAsString(event);
                  kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
                  ...
              }

              @Async("eventTaskExecutor")
              @TransactionalEventListener
              public void on(RoleUpdatedEvent event) { ... }

              @Async("eventTaskExecutor")
              @EventListener
              public void on(S3UploadFailedEvent event) { ... }
          }
          ```

            - Spring Event를 Kafka 메시지로 변환해 전송하는 중계 구조입니다.

              ![60f20el76-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14106&version=1&directory=/60f20el76-image.png&name=60f20el76-image.png)

    - [ ] Kafka Console을 통해 Kafka 이벤트가 잘 발행되는지 확인해보세요.

        1. `broker` 컨테이너 쉘 접속

           ```shell
           docker exec -it -w /opt/kafka/bin broker sh
           ```

        2. 토픽 리스트 확인 (실행위치: `/opt/kafka/bin`)

           ```shell
           ./kafka-topics.sh --list --bootstrap-server broker:29092

           __consumer_offsets
           discodeit.MessageCreatedEvent
           ...
           ```

        3. 특정 토픽 이벤트 구독 및 대기 (실행위치: `/opt/kafka/bin`)

           ```shell
           ./kafka-console-consumer.sh --topic discodeit.MessageCreatedEvent --from-beginning --bootstrap-server broker:29092

           ...
           ```
    
    **토픽 리스트 확인**  
    - `docker exec -it -w /opt/kafka/bin broker sh`: broker 컨테이너 안에 /opt/kafka/bin 위치로 터미널 접속
        - /opt/kafka/bin 안에 운영용 CLI 스크립트가 들어 있음
    - `./kafka-topics.sh --list --bootstrap-server broker:29092`
      - ./kafka-topics.sh : 토픽을 관리(CRUD)하는 CLI 도구
      - --list : 브로커에 존재하는 모든 토픽 이름 나열 옵션
      - --bootstrap-server borker:29092 : 접속할 Kafka 브로커의 주소. 클리어언트가 클러스터에 처음 진입하는 진입점
        - 같은 Docker 네트워크 안이므로 KAFKA_ADVERTISED_LISTENERS(Kafka가 클라이언트에게 알려주는 주소)의 
          내부용 주소인 PLAINTEXT://broker:29092를 사용
    ```shell
    /opt/kafka/bin $  ./kafka-topics.sh --list --bootstrap-server broker:29092
    __consumer_offsets
    discodeit.MessageCreatedEvent
    discodeit.RoleUpdatedEvent
    discodeit.S3UploadFailedEvent
    ```
    - __consumer_offsets : Kafka가 자동으로 만드는 내부 시스템 토픽. 각 컨슈머 그룹이 어디까지 읽었는지(offset)를 저장함. Kafka가 알아서 관리함.
    - discodeit.* : 애플리케이션이 발행하는 도메인 이벤트 토픽.
    
    **discodeit.MessageCreatedEvent**  
    - `./kafka-console-consumer.sh` : 토픽의 메시지를 터미널에서 직접 consume해서 출력해주는 디버깅용 컨슈머
    - `--topic discodeit.MessageCreatedEvent` : 구독할 대상 토픽 이름
    - `--from-beginning` : 토픽에 쌓인 맨 처음 메시지부터 전부 읽기. 이 옵션이 없으면 명령 실행 이후 새로 들어오는 메시지만 보여줌 
    - `--bootstrap-server broker:29092` : 접속할 브로커 주소
    ```shell
    /opt/kafka/bin $ ./kafka-console-consumer.sh \
    > --topic discodeit.MessageCreatedEvent \
    > --from-beginning \
    > --bootstrap-server broker:29092
    {"messageId":"93193475-bd05-45df-889a-b6785062866b","channelId":"aed40b8e-043e-4018-bdec-ce907af89612","authorId":"19c140e8-99c7-4ac1-ba21-991dbf997f83","authorUsername":"admin","content":"새로운 메시지","channelName":"과일장사"}
    {"messageId":"4239af0e-626d-440f-bc32-32b28484300d","channelId":"f625e3da-7879-4f3e-a784-b39ecc9507e8","authorId":"19c140e8-99c7-4ac1-ba21-991dbf997f83","authorUsername":"admin","content":"알고리즘 공부","channelName":"알고리즘"}
    
    ...
    
    ```
    - 애플리케이션이 JSON으로 직렬화해서 발행한 MessageCreatedEvent의 내용을 확인할 수 있다.
    
    **discodeit.RoleUpdatedEvent**
    ```shell
    /opt/kafka/bin $ ./kafka-console-consumer.sh \
    > --topic discodeit.RoleUpdatedEvent \
    > --from-beginning \
    > --bootstrap-server broker:29092
    {"userId":"bc77d311-0407-499b-b5b1-2196fe3ef90e","oldRole":"CHANNEL_MANAGER","newRole":"USER"}
    {"userId":"bc77d311-0407-499b-b5b1-2196fe3ef90e","oldRole":"USER","newRole":"CHANNEL_MANAGER"}
    
    ...
    
    ```

    - [ ] Kafka 토픽을 구독해 알림을 생성하는 리스너를 구현하세요.

        - 이 리스너는 메인 서비스와 별도의 서버로 구성된 알림 서비스라고 가정합니다.

        - `NotificationRequiredTopicListener`를 구현하세요.

          ```java
          package com.sprint.mission.discodeit.event.kafka;

          @Slf4j
          @RequiredArgsConstructor
          @Component
          public class NotificationRequiredTopicListener {
              ...
              private final ObjectMapper objectMapper;

              @KafkaListener(topics = "discodeit.MessageCreatedEvent")
              public void onMessageCreatedEvent(String kafkaEvent) {
                  try {
                      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
                      ...
                  } catch (JsonProcessingException e) {
                      throw new RuntimeException(e);
                  }
              }

              @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
              public void onRoleUpdatedEvent(String kafkaEvent) { ... }

              @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
              public void onS3UploadFailedEvent(String kafkaEvent) { ... }
          }
          ```

        - 기존 `@EventListener` 기반 로직을 제거하고 `@KafkaListener`로 대체하세요.


### Redis Cache 도입하기

- 대용량 트래픽을 감당하기 위해 서버의 인스턴스를 여러 개로 늘렸다고 가정해봅시다.

- `Caffeine`과 같은 로컬 캐시는 서로 다른 서버에서 더 이상 활용할 수 없습니다. 따라서 `Redis`를 통해 전역 캐시 저장소를 구성합니다.

- [ ] **Redis 환경을 구성하세요.**

    - Docker Compose를 활용해 Redis를 구동하세요.

      ```yaml
      # docker-compose-redis.yml
      # https://developer.confluent.io/confluent-tutorials/kafka-on-docker/#the-docker-compose-file
      services:
        redis:
          image: redis:7.2-alpine
          container_name: redis
          ports:
            - "6379:6379"
          volumes:
            - redis-data:/data
          command: redis-server --appendonly yes

      volumes:
        redis-data:
      ```

      ```shell
      docker compose -f docker-compose-redis.yml up -d
      ```

    - Redis 의존성을 추가하고, `application.yml`에 Redis 설정을 추가하세요.

      ```groovy
      implementation 'com.github.ben-manes.caffeine:caffeine'
      implementation 'org.springframework.boot:spring-boot-starter-data-redis'
      ```

      ```yaml
      # application.yaml
      spring:
        ...
        cache:
          type: caffeine
          type: redis
          cache-names:
            - channels
            - notifications
            - users
          caffeine:
            spec: >
              maximumSize=100,
              expireAfterAccess=600s,
              recordStats
          redis:
            enable-statistics: true
        data:
          redis:
            host: ${REDIS_HOST:localhost}
            port: ${REDIS_PORT:6379}
      ```

    - 직렬화 설정을 위해 다음과 같이 Bean을 선언하세요.

      ```java
      // CacheConfig
      @Bean
      public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
          ObjectMapper redisObjectMapper = objectMapper.copy();
          redisObjectMapper.activateDefaultTyping(
              LaissezFaireSubTypeValidator.instance,
              DefaultTyping.EVERYTHING,
              As.PROPERTY
          );

          return RedisCacheConfiguration.defaultCacheConfig()
              .serializeValuesWith(
                  RedisSerializationContext.SerializationPair.fromSerializer(
                      new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                  )
              )
              .prefixCacheNameWith("discodeit:")
              .entryTtl(Duration.ofSeconds(600))
              .disableCachingNullValues();
      }
      ```

- [ ] DataGrip을 통해 Redis에 저장된 캐시 정보를 조회해보세요.

```shell
docker exec -it redis redis-cli
```

캐시 초기화
```shell
127.0.0.1:6379> FLUSHALL
OK
127.0.0.1:6379> KEYS 'discodeit*'
(empty array)
```

- 캐시 채워진 것 확인
- TTL 확인 (캐시 만료까지 얼마 남았는지 확인)
- 캐시 값 구조
- @CacheEvict를 통한 캐시 무효화 확인 (userChannels 키 사라짐으로 확인)
```shell
127.0.0.1:6379> KEYS 'discodeit*' 
1) "discodeit:users::SimpleKey []"
2) "discodeit:userNotifications::93185263-f589-4022-a273-92913d340d2b"
3) "discodeit:userChannels::93185263-f589-4022-a273-92913d340d2b"
127.0.0.1:6379> TTL "discodeit:users::SimpleKey []"
(integer) 577
127.0.0.1:6379> GET "discodeit:users::SimpleKey []"
"[\
  "java.util.ImmutableCollections$ListN\",
  [
    {
      \"@class\":\"com.sprint.mission.discodeit.dto.data.UserDto\",
      \"id\":[\"java.util.UUID\",\"93185263-f589-4022-a273-92913d340d2b\"],
      \"username\":\"admin\",
      \"email\":\"admin@gmail.com\",
      \"profile\":null,
      \"online\":true,
      \"role\":[\"com.sprint.mission.discodeit.entity.UserRole\",\"ADMIN\"]
    },
    {
      \"@class\":\"com.sprint.mission.discodeit.dto.data.UserDto\",
      \"id\":[\"java.util.UUID\",\"a2ef0e70-8d98-4313-b92b-3179ca999643\"],
      \"username\":\"abc\",
      \"email\":\"abc@gmail.com\",
      \"profile\":{
        \"@class\":\"com.sprint.mission.discodeit.dto.data.BinaryContentDto\",
        \"id\":[\"java.util.UUID\",\"a7c575b1-5193-4a68-85c1-2ad6f78540c6\"],
        \"fileName\":\"Blueberry.jpg\",
        \"size\":[\"java.lang.Long\",73894],
        \"contentType\":\"image/jpeg\",
        \"status\":[\"com.sprint.mission.discodeit.entity.BinaryContentStatus\",\"SUCCESS\"]
      },
      \"online\":false,
      \"role\":[\"com.sprint.mission.discodeit.entity.UserRole\",\"USER\"]
    },
    ...
  ]
] 
  
127.0.0.1:6379> KEYS 'discodeit:userChannels*'
1) "discodeit:userChannels::93185263-f589-4022-a273-92913d340d2b"
127.0.0.1:6379> KEYS 'discodeit:userChannels*'
(empty array)
```

## 🔄 주요 변경사항

## 📸 스크린샷

## 🙇🏽‍♂️ 멘토에게
