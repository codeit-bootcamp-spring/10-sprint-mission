# sprint mission 2

### 목표

> - 채팅 서비스의 도메인 모델을 설계하고 이를 Java 로 구현한다.
> - 인터페이스 설계 및 구현한다.
> - 싱글톤 패턴을 구현한다.
> - Java Collections API 를 활용한다.
> - [심화] *모듈간 의존관계를 파악하고 팩토리 패턴을 활용하여 이를 줄일 수 있다.*

### 요구사항

- 도메인 모델 정의
  - User
  - Channel
  - Message
  - 공통
    - id(UUID)
    - createdAt(long, unix timestamp)
    - updatedAt(long, unix timestamp)
  - getter/update 구현
- 서비스 설계
  - 도메인 모델별 CRUD 기능을 인터페이스로 정의
  - `Java Collections API` 를 활용하여 구현
- 메인 클래스 설계
  - `JavaApplication` 클래스를 선언하고 도메인 별 서비스 구현체를 테스트
  - 등록
  - 조회
  - 수정 및 검사
  - 삭제 및 검사

### 구현

- entity
  - `Entity` 아래 3가지 entity 의 공통 기능 구현
    - DTO class
    - copy: data integrity
    - update: userId, channelId, message 변경
    - uuid, createdAt, updatedAt 은 외부 요청에 의해 변경할 이유가 없으므로 getter 만 구현
  - `User`
    - userId: user 들에게 보여지는 이름, 변경 가능하며 동일한 id 도 가능
  - `Channel`
    - channelId: channel 이름, 변경 가능
  - `Message`
    - message: 수정 가능
- model
  - `Model` 각 `Entity` 별로 `Map` 에 <UUID, Entity> 저장
  - `RelationModel` `Entity` 간 관계를 저장
    - ```
      RelationModel<User, Message> // 각 user 가 생성한 message 들을 저장
      ```
  - 공통 부분
    - get, getAll(`Model`), remove, add, update, contain
