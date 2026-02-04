# 3️⃣ 스프린트 미션 3
<details>
<summary>스프린트 미션 2</summary>

# [SB] 스프린트 미션 2
## 📝 요구사항 - 1차
### 1️⃣ 기본 요구사항
**📍프로젝트 초기화**
* [x] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
    * [x] IntelliJ에서 제공하는 프로젝트 템플릿 중 **Java**를 선택합니다.
    * [x] 프로젝트의 경로는 **스프린트 미션 리포지토리의 경로와 같게** 설정합니다.  
      예를 들어 스프린트 미션 리포지토리의 경로가 `/some/path/1-sprint-mission` 이라면:
        - **Name**: `1-sprint-mission`
        - **Location**: `/some/path`

    * [x] `Create Git Repository` 옵션은 체크하지 않습니다.
    * [x] **Build system**은 **Gradle**을 사용합니다.
    * [x] **Gradle DSL**은 **Groovy**를 사용합니다.
    * [x] **JDK 17**을 선택합니다.
    * [x] **GroupId**는 `com.sprint.mission`으로 설정합니다.
    * [x] **ArtifactId**는 수정하지 않습니다.
    * [x] `.gitignore`에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 `.idea` 디렉토리를 추가합니다.
        ```text
        ...
        .idea
        ...
        ```
**📍도메인 모델링**
* [x] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
    * [x] 패키지명: com.sprint.mission.discodeit.entity
    * [x] 도메인 모델 정의
        * [x] 공통
            * [x] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언합니다.
            * [x] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
        * [x] User
        * [x] Channel
        * [x] Message
    * [x] 생성자
        * [x] id는 생성자에서 초기화하세요.
        * [x] createdAt는 생성자에서 초기화하세요.
        * [x] id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.
    * [x] 메서드
        * [x] 각 필드를 반환하는 Getter 함수를 정의하세요.
        * [x] 필드를 수정하는 update 함수를 정의하세요.

**📍서비스 설계 및 구현**
* [x] 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언하세요.
    * [x] 인터페이스 패키지명: com.sprint.mission.discodeit.service
    * [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Service
* [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    * [x] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
    * [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
    * [x] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
    * [x] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메서드를 구현하세요.

**📍메인 클래스 구현**
* [x] 메인 메서드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
* [x] 등록
* [x] 조회(단건, 다건)
* [x] 수정
* [x] 수정된 데이터 조회
* [x] 삭제
* [x] 조회를 통해 삭제되었는지 확인
### 2️⃣ 심화 요구 사항
**📍서비스 간 의존성 주입**
* [x] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요.  
  힌트: Message를 생성할 때 연관된 도메인 모델 데이터 확인하기

## 📝 요구사항 - 2차
### 1️⃣ 기본 요구사항
**📍File IO를 통한 데이터 영속화**
* [x]  다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    * [x]  클래스 패키지명: com.sprint.mission.discodeit.service.file
    * [x]  클래스 네이밍 규칙: File[인터페이스 이름]
    * [x]  JCF 대신 FileIO와 객체 직렬화를 활용해 메서드를 구현하세요.  
      [객체 직렬화/역직렬화 가이드](https://codeit.notion.site/13b6fd228e8d80c6b144cdfbf518a9f7?pvs=21)
* [x]  Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

**📍서비스 구현체 분석**
* [x] JCF\*Service 구현체와 File\*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요.
    * [x] "비즈니스 로직"과 관련된 코드를 식별해보세요.
    * [x] "저장 로직"과 관련된 코드를 식별해보세요.

**📍레포지토리 설계 및 구현**
* [x] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
    * [x] 인터페이스 패키지명: com.sprint.mission.discodeit.repository
    * [x] 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository
* [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
    * [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf
    * [x] 클래스 네이밍 규칙: JCF[인터페이스 이름]
    * [x] 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
* [x] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
    * [x] 클래스 패키지명: com.sprint.mission.discodeit.repository.file
    * [x] 클래스 네이밍 규칙: File[인터페이스 이름]
    * [x] 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.
### 2️⃣ 심화 요구 사항
**📍관심사 분리를 통한 레이어 간 의존성 주입**
* [x] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
    * [x] 클래스 패키지명: com.sprint.mission.discodeit.service.basic
    * [x] 클래스 네이밍 규칙: Basic[인터페이스 이름]
    * [x] 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.
    * [x] 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.
    * [x] "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)
* [x] Basic*Service 구현체를 활용하여 테스트해보세요.
    * 코드 템플릿
  ```Java
  public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }
    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserService userService;
        ChannelService channelService;
        MessageService messageService;

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
  } 
  ```
    * [x]  JCF*Repository  구현체를 활용하여 테스트해보세요.
    * [x]  File*Repository 구현체를 활용하여 테스트해보세요.
* [x] 이전에 작성했던 코드(JCF*Service 또는 File*Service)와 비교해 어떤 차이가 있는지 정리해보세요.

## 🔄 주요 변경사항

## 📸 스크린샷

## 🙇🏽‍♂️ 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.

---
</details>

---

## 🏔️ 프로젝트 마일스톤
- Java 프로젝트를 Spring 프로젝트로 마이그레이션
- 의존성 관리를 IoC Container에 위임하도록 리펙토링
- 비즈니스 로직 고도화
----
## 📝 요구사항
### ✏️ 기본 요구사항
**📌 Spring 프로젝트 초기화**
- [x] Spring Initializr를 통해 zip 파일을 다운로드하세요.
  - [x] 빌드 시스템은 Gradle-Groovy를 사용합니다.
  - [x] 언어는 Java 17를 사용합니다.
  - [x] Spring Boot 버전은 `3.4.0`입니다.
  - [x] GroupId는 `com.sprint.mission`입니다.
  - [x] ArtifactId와 Name은 `discodeit`입니다.
  - [x] packaging 형식은 `Jar`입니다.
  - [x] Dependency를 추가합니다.
    - [x] Lombok
    - [x] Spring Web
- [x] zip 파일을 압축해제하고 원래 진행 중이던 프로젝트에 붙여넣기하세요. 일부 파일은 덮어쓰기 할 수 있습니다.
- [x] `application.properties`파일을`yaml`형식으로 변경하세요.
- [x] `DiscodeitApplication`의 main 메서드를 실행하고 로그를 확인해보세요.

**📌 Bean 선언 및 테스트**
- [x] File*Repository 구현체를 Repository 인터페이스의 Bean으로 등록하세요.
- [x] Basic*Service 구현체를 Service 인터페이스의 Bean으로 등록하세요.
- [x] `JavaApplication`에서 테스트 했던 코드를 `DiscodeitApplication`에서 테스트해보세요.
  - [x] `JavaApplication`의 main 메서드를 제외한 모든 메서드를`DiscodeitApplication`클래스로 복사하세요.
  - [x] `JavaApplication`의 main 메서드에서 Service를 초기화하는 코드를 Spring Context를 활용하여 대체하세요.
    ```java
    // JavaApplication
    public static void main(String[] args) {
        // 레포지토리 초기화
        // ...
        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
    
        // ...
    }
    
    // DiscodeitApplication
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        // 서비스 초기화
        // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
        UserService userService;
        ChannelService channelService;
        MessageService messageService;
    
        // ...
    }
    ```
  - [x] `JavaApplication`의 main 메서드의 셋업, 테스트 부분의 코드를 `DiscodeitApplication`클래스로 복사하세요
    ```java
    public static void main(String[] args) {
        // ...
        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
    ```
**📌 Spring 핵심 개념 이해하기**
- [x] `JavaApplication`과 `DiscodeitApplication`에서 Service를 초기화하는 방식의 차이에 대해 다음의 키워드를 중심으로 정리해보세요.
  - IoC Container
  - Dependency Injection
  - Bean
```markdown
# 답안

* IoC Container
- JavaApplication: 개발자가 직접 의존성을 주입하고 관리한다. 제어권이 개발자에게 있다.
- DiscodeitApplication: SpringApplication.run()을 통해 스프링의 IoC 컨테이너가 실행된다.  
  컨테이너가 실행 정보를 바탕으로 객체를 생성하고 의존성을 자동으로 연결한다.  
  객체 관리의 주도권이 프레임워크에게 있다. 
  
* Dependency Injection
- JavaApplication: 서비스 클래스를 생성할 때, 필요한 레포지토리 객체들을 생성자 인자로 직접 전달해서 의존성을 주입한다. 
- DiscodeitApplication: 스프링이 빈으로 등록된 클래스 간의 의존 관계를 분석하여 자동으로 연결해 준다.
  
* Bean
- JavaApplication: 일반적인 Java 객체들을 사용한다.
- DiscodeitApplication: 스프링 컨테이너에 의해 생성되고 관리되는 객체를 Bean이라고 부른다.
  @Service나 @Repository 처럼 내부에 @Component를 포함하고 있는 어노테이션이 붙은 클래스들은 스프링에 의해 빈으로 등록된다.

```
**📌 Lombok 적용**
- [x] 도메인 모델의 getter 메서드를 @Getter로 대체해보세요.
- [x] Basic*Service의 생성자를 @RequiredArgsConstructor로 대체해보세요

**📌 비즈니스 로직 고도화**
- [x] 다음의 기능 요구 사항을 구현하세요.
---
## 🔥 추가 기능 요구 사항
**시간 타입 변경하기**
- [x] 시간을 다루는 필드의 타입은 `Instant`로 통일합니다.
  - 기존에 사용하던 Long보다 가독성이 뛰어나며, 시간대(Time Zone) 변환과 정밀한 시간 연산이 가능해 확장성이 높습니다.

**새로운 도메인 추가하기**
- 도메인 모델 간 참조 관계를 참고하세요.   
<img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/8y8gqa7me-image.png&name=8y8gqa7me-image.png" width="436" height="387"/>

   - [x] 공통: 앞서 정의한 도메인 모델과 동일하게 공통 필드(`id`, `createdAt`, `updatedAt`)를 포함합니다.
   - [x] `ReadStatus`
     - 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 사용합니다.
   - [x] `UserStatus`
     - 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
     - [x] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메서드를 정의하세요.
       - 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
   - [x] `BinaryContent`
     - 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
     - [x] 수정 불가능한 도메인 모델로 간주합니다. 따라서 `updateAt` 필드는 정의하지 않습니다.
     - [x] `User`, `Message` 도메인 모델과의 의존 관계 방향성을 잘 고려하여 `id` 참조 필드를 추가하세요.
   - [x] 각 도메인 모델 별 레포지토리 인터페이스를 선언하세요.
     - 레포지토리 구현체(File, JCF)는 아직 구현하지 마세요. 이어지는 서비스 고도화 요구사항에 따라 레포지토리 인터페이스에 메서드가 추가될 수 있어요.

**DTO 활용하기**
<details>
<summary>DTO란?</summary>

DTO(Data Transter Object)란 데이터를 전달하기 위한 단순한 객체를 말합니다.  
이어지는 요구사항을 해결하려다보면, 도메인 모델의 일부 정보만 포함하거나, 여러 도메인 모델의 정보를 합친 데이터 모델이 필요한 경우가 생길거에요.  
이런 경우에 도메인 모델을 특정 상황만을 위해 수정하기 보다는 DTO를 정의해서 해결하는 것이 바람직합니다.  
뿐만 아니라 메서드 파라미터가 많아지거나, 그룹핑하고 싶을 때에도 유용할 수 있어요.

DTO를 정의할 때는 class 대신 Record를 활용하면 더 편리합니다.
```java
public record MyDto(
        UUID id,
        String prop1,
        Long prop2
) {}

public record MyCreateRequest(
        String param1,
        Long param2
) {}

public MyDto create(MyCreateRequest request) { ... }

```
</details>

**UserService 고도화**
- **고도화**
  - `create`  
    - [x] 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
      - 유저를 등록하기 위해 필요한 파라미터, 프로필 이미지를 등록하기 위해 필요한 파라미터 등
    - [x] `username`과 `email`은 다른 유저와 같으면 안됩니다.
    - [x] `UserStatus`를 같이 생성합니다.
  - `find`, `findAll`
    - DTO를 활용하여:
      - [x] 사용자의 온라인 상태 정보를 같이 포함하세요.
      - [x] 패스워드 정보는 제외하세요.
  - `update`
    - [x] 선택적으로 프로필 이미지를 대체할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
      - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - `delete`
    - [x] 관련된 도메인도 같이 삭제합니다.
      - `BinaryContent`(프로필), `UserStatus`
- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.
  <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/jv2bxgd12-image.png&name=jv2bxgd12-image.png" width="424" height="155"/>

### AuthService 구현 
- `login`
  - [x] `username`, `password`과 일치하는 유저가 있는지 확인합니다.
    - [x] 일치하는 유저가 있는 경우: 유저 정보 반환
    - [x] 일치하는 유저가 없는 경우: 예외 발생
  - [x] DTO를 활용해 파라미터를 그룹화합니다.
- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.  
  <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/usf34n0k1-image.png&name=usf34n0k1-image.png" width="140" height="217"/>

**ChannelService 고도화**
- **고도화**
  - `create`
    - PRIVATE 채널과 PUBLIC 채널을 생성하는 메서드를 분리합니다.
    - [x] 분리된 각각의 메서드를 DTO를 활용해 파라미터를 그룹화합니다.
    - PRIVATE 채널을 생성할 때:
      - [x] 채널에 참여하는 `User`의 정보를 받아 `User`별 `ReadStatus`정보를 생성합니다.
      - [x] `name`과 `description` 속성은 생략합니다.
    - [x] PUBLIC 채널을 생성할 때에는 기존 로직을 유지합니다.
  - `find`
    - DTO를 활용하여:
      - [x] 해당 채널의 가장 최근 메시지의 시간 정보를 포함합니다.
      - [x] PRIVATE 채널인 경우 참여한 `User`의`id`정보를 포함합니다.
  - `findAll`
    - DTO를 활용하여:
      - [x] 해당 채널의 가장 최근 메시지와 시간 정보를 포함합니다.
      - [x] PRIVATE 채널인 경우 참여한 `User`의`id`정보를 포합합니다.
      - [x] 특정 `User`가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메서드 명을 변경합니다. `findAllByUserId`
      - [x] PUBLIC 채널 목록은 전체 조회합니다.
      - [x] PRIVATE 채널은 조회한 User가 참여한 채널만 조회합니다.
  - `update`
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
      - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    - [x] PRIVATE 채널은 수정할 수 없습니다.
  - `delete`
    - [x] 관련된 도메인도 같이 삭제합니다.
      - `Message`, `ReadStatus`
- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.  
    <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/5f221mj11-image.png&name=5f221mj11-image.png" width="424" height="162"/>

**MessageService 고도화**
- **고도화**
  - `create`
    - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
  - `findAll`
    - [x] 특정 `Channel`의 Message 목록을 조회하도록 조회 조건을 추가하고, 메서드 명을 변경합니다. `findallByChannelId`
  - `update`
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
      - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - `delete`
    - [x] 관련된 도메인도 같이 삭제합니다.
      - 첨부파일(BinaryContent)

- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.  
    <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/3lpcv58wo-image.png&name=3lpcv58wo-image.png" width="424" height="118"/>

**ReadStatusService 구현**
  - `create`
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
    - [x] 관련된 `Channel`이나 `User`가 존재하지 않으면 예외를 발생시킵니다.
    - [x] 같은 `Channel`과 `User`와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
  - `find`
    - [x] `id`로 조회합니다.
  - `findAllByUserId`
    - [x] `userId`를 조건으로 조회합니다.
  - `update`
    - [x] DTO를 활용해 파라미터를 그룹화합니다.
      - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
  - `delete`
    - [x] `id`로 삭제합니다.


- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.    
    <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/dvbyymgav-image.png&name=dvbyymgav-image.png" width="424" height="164"/>
  
**UserStatusService 고도화**
   - `create`
     - [x] DTO를 활용해 파라미터를 그룹화합니다.
     - [x] 관련된 `User`가 존재하지 않으면 예외를 발생시킵니다.
     - [x] 같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
   - `find`
     - [x] `id`로 조회합니다.
   - `findAll`
     - [x] 모든 객체를 조회합니다.
   - `update`
     - [x] DTO를 활용해 파라미터를 그룹화합니다.
       - 수정 대상 객체의 `id` 파라미터, 수정할 값 파라미터
   - `updateByUserId`
     - [x] `userId`로 특정 `User`의 객체를 업데이트합니다.
   - `delete`
     - [x] `id`로 삭제합니다.


- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.  
    <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/wms3box4u-image.png&name=wms3box4u-image.png" width="424" height="252"/>

**BinaryContentService 구현**
   - `create`
     - [x] DTO를 활용해 파라미터를 그룹화합니다.
   - `find`
     - [x] `id`로 조회합니다.
   - `findAllByIdIn`
     - [x] `id` 목록으로 조회합니다.
   - `delete`
     - [x] `id`로 삭제합니다.


- **의존성**
  - 같은 레이어 간 의존성 주입은 순환 참조 방지를 위해 지양합니다. 다른 Service 대신 필요한 Repository 의존성을 주입해보세요.  
    <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/1vjxng9jx-image.png&name=1vjxng9jx-image.png" width="424" height="441"/>
  
**새로운 도메인 Repository 구현체 구현**
- [x] 지금까지 인터페이스로 설계한 각각의 Repository를 JCF, File로 각각 구현하세요.  
  <img src="https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=11890&version=1&directory=/w9rzlpt8k-image.png&name=w9rzlpt8k-image.png" width="436" height="342"/>

---
### ✏️ 심화 요구사항
**Bean 다루기**
- [x] Repository 구현체 중에 어떤 구현체를 Bean으로 등록할지 Java 코드의 변경 없이 application.yaml 설정 값을 통해 제어해보세요.
  ```yaml
  # application.yaml
  discodeit:
      repository: 
          type: jcf   # jcf | file
  ```
  - [x] `discodeit.repository.type` 설정값에 따라 Repository 구현체가 정해집니다.
    - [x] 값이 `jcf`이거나 없으면 JCF*Repository 구현체가 Bean으로 등록되어야 합니다.
    - [x] 값이 `file`이면 File*Repository 구현체가 Bean으로 등록되어야 합니다.
- [x] File*Repository 구현체의 파일을 저장할 디렉토리 경로를 application.yaml 설정 값을 통해 제어해보세요.
  ```yaml
  # application.yaml
  discodeit:
      repository: 
          type: jcf   # jcf | file
          file-directory: .discodeit
  ```
---
## 🔄 주요 변경사항

## 📸 스크린샷
<img src="" width="" height=""/>

## 🙇🏽‍♂️ 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
