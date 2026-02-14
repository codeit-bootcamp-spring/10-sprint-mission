## 기본 요구사항

### Spring 프로젝트 초기화
- [x] Spring Initializr를 통해 zip 파일을 다운로드
- [x] 빌드 시스템: Gradle (Groovy)
- [x] 언어: Java 17
- [x] Spring Boot 버전: 3.4.0
- [x] GroupId: com.sprint.mission
- [x] ArtifactId / Name: discodeit
- [x] Packaging 형식: Jar
- [x] Dependency 추가
  - [x] Lombok
  - [x] Spring Web
- [x] zip 파일을 압축 해제 후 기존 프로젝트에 병합 (일부 파일 덮어쓰기)
- [x] application.properties → application.yml로 변경
- [x] DiscodeitApplication의 main 메서드 실행 및 로그 확인

---

### Bean 선언 및 테스트

- [x] File*Repository 구현체를 Repository 인터페이스 타입의 Bean으로 등록
- [x] Basic*Service 구현체를 Service 인터페이스 타입의 Bean으로 등록
- [x] JavaApplication에서 테스트했던 코드를 DiscodeitApplication에서 실행
- [x] JavaApplication의 main 메소드를 제외한 모든 메소드를 DiscodeitApplication 클래스로 복사
- [x] JavaApplication의 main 메소드에서 Service를 초기화하던 코드를
      Spring Context 기반 Bean 조회 방식으로 대체
- [x] JavaApplication의 셋업 및 테스트 로직을 DiscodeitApplication으로 이관

---

### Spring 핵심 개념 이해하기

- [x] JavaApplication과 DiscodeitApplication에서 Service 초기화 방식의 차이를 다음 키워드를 중심으로 정리
  - IoC Container
    - JacaApplication: new와 ServiceFactory로 Repository*Service 객체를 직접 생성하고 연결했었다. 즉, 객체 생성과 의존성 관리의 제어권이 애플리케이션 코드에 있었다.
    - DiscodeitApplication: SpringApplication.run()으로 Spring IoC Container가 생성되고, 컨테이너가 객체의 생성과 관리를 책임지게 되었다. 즉, 객체 생성과 제어권이 Spring으로 역전(IoC) 되었다.

  - Dependency Injection
    - JacaApplication: new BasicUserService(userRepository)처럼 필요한 의존성을 개발자가 직접 만들어 수동으로 주입했었다.
    - DiscodeitApplication: @Repository, @Service로 Bean을 등록하고, Spring이 생성자 주입 등을 통해 필요한 의존성을 자동 주입 한다. 이로 인하여 서비스 코드는 생성 로직에서 분리되고 결합도가 낮아졌다.

  - Bean
    - JacaApplication: new로 만든 객체는 일반 객체로, 별도의 컨테이너가 생명주기를 관리하지 않았다.
    - DiscodeitApplication: @Repository, @Service 등으로 등록된 객체는 Spring Bean이 되며, IoC Container가 생성과 주입, 생명주기를 관리하게 되었다. 애플리케이션은 context.getBean()으로 Bean을 조회하여 사용할 수 있다.

---

### Lombok 적용 

- [x] 도메인 모델의 getter 메소드를 @Getter로 대체
- [x] Basic*Service 생성자를 @RequiredArgsConstructor로 대체

---

## 추가 기능 요구사항
### 시간 타입 변경하기
- [x] 시간을 다루는 필드의 타입은 Instant로 통일합니다.
  - 기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.

### 새로운 도메인 추가하기
- 도메인 모델 간 참조 관계를 참고하세요.

- [x] 공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(id, createdAt, updatedAt)를 포함합니다.
- [x] ReadStatus
  - 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 
  - 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
- [x] UserStatus
  - 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 
  - 사용자의 온라인 상태를 확인하기 위해 활용합니다.
- [x] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
  - 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
- [x] BinaryContent
  - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 
  - 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
  - [x] 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
  - [x] User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.
- [x] 각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.
  - 레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 
  - 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메소드가 추가될 수 있어요.

---

### UserService 고도화
- 고도화
  - create
    - [x] 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
    - [x] username과 email은 다른 유저와 같으면 안됩니다.
    - [x] UserStatus를 같이 생성합니다.
  - find, findAll
    - DTO를 활용하여:
      - [x] 사용자의 온라인 상태 정보를 같이 포함하세요.
      - [x] 패스워드 정보는 제외하세요.
  - update
    - [x] 선택적으로 프로필 이미지를 대체할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - delete
    - [x] 관련된 도메인도 같이 삭제합니다.BinaryContent(프로필), UserStatus
  - 의존성
    - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### AuthService 구현
- login
  - [x] username, password과 일치하는 유저가 있는지 확인합니다.
  - [x] 일치하는 유저가 있는 경우: 유저 정보 반환
  - [x] 일치하는 유저가 없는 경우: 예외 발생
  - [x] DTO를 활용해 파라미터를 그룹화합니다.
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### ChannelService 고도화
- 고도화
  - create
    - PRIVATE 채널과 PUBLIC 채널을 생성하는 메소드를 분리합니다.
    - [x] 분리된 각각의 메소드를 DTO를 활용해 파라미터를 그룹화합니다.
    - PRIVATE 채널을 생성할 때:
      - [x] 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성합니다.
      - [x] name과 description 속성은 생략합니다.
    - PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
  - find
    - DTO를 활용하여:
      - [x] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
      - [x] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
  - findAll
    - DTO를 활용하여:
      - [x] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
      - [x] PRIVATE 채널인 경우 참여한 User의 id 정보를 포함합니다.
      - [x] 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
      - [x] PUBLIC 채널 목록은 전체 조회합니다.
      - [x] PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
  - update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] PRIVATE 채널은 수정할 수 없습니다.
  - delete
    - [x] 관련된 도메인도 같이 삭제합니다.Message, ReadStatus
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### MessageService 고도화
- 고도화
  - create
    - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
  - findAll
    - [x] 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findallByChannelId
  - update
    - [x] DTO를 활용해 파라미터를 그룹화합니다.수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - delete
    - [x] 관련된 도메인도 같이 삭제합니다.첨부파일(BinaryContent)
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### ReadStatusService 구현
- create
  - [x] DTO를 활용해 파라미터를 그룹화합니다.
  - [x] 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
  - [x] 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
  - [x] id로 조회합니다.
- findAllByUserId
  - [x] userId를 조건으로 조회합니다.
- update
  - [x] DTO를 활용해 파라미터를 그룹화합니다.수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- delete
  - [x] id로 삭제합니다.
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### UserStatusService 고도화
- create
  - [x] DTO를 활용해 파라미터를 그룹화합니다.
  - [x] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
  - [x] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
- find
  - [x] id로 조회합니다.
- findAll
  - [x] 모든 객체를 조회합니다.
- update
  - [x] DTO를 활용해 파라미터를 그룹화합니다.수정 대상 객체의 id 파라미터, 수정할 값 파라미터
- updateByUserId
  - [x] userId 로 특정 User의 객체를 업데이트합니다.
- delete
  - [x] id로 삭제합니다.
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### BinaryContentService 구현
- create
  - [x] DTO를 활용해 파라미터를 그룹화합니다.
- find
  - [x] id로 조회합니다.
- findAllByIdIn
  - [x] id 목록으로 조회합니다.
- delete
  - [x] id로 삭제합니다.
- 의존성
  - [x] 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.

---

### 새로운 도메인 Repository 구현체 구현
- [x] 지금까지 인터페이스로 설계한 각각의 Repository를 JCF, File로 각각 구현하세요.

---

## 심화 요구사항
### Bean 다루기
- [x] Repository 구현체 중 어떤 구현체를 Bean으로 등록할지 Java 코드의 변경 없이 application.yaml 설정 값을 통해 제어해보세요.
- [x] discodeit.repository.type 설정값에 따라 Repository 구현체가 결정되어야 합니다.
- [x] discodeit.repository.type 값이 jcf 이거나 설정 값이 없는 경우 JCF*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x] discodeit.repository.type 값이 file 인 경우 File*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x] File*Repository 구현체가 사용하는 파일 저장 디렉토리 경로를 application.yaml 설정 값을 통해 제어해보세요.
