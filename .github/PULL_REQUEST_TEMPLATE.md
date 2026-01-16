
## 요구사항

### 기본 요구사항

#### 프로젝트 초기화
- [x] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
    - [x] IntelliJ에서 제공하는 프로젝트 템플릿 중 Java를 선택합니다.
    - [x] 프로젝트의 경로는 스프린트 미션 리포지토리의 경로와 같게 설정합니다.
    - [x] Create Git Repository 옵션은 체크하지 않습니다.
    - [x] Build system은 Gradle을 사용합니다. Gradle DSL은 Groovy를 사용합니다.
    - [x] JDK 17을 선택합니다.
    - [x] GroupId는 com.sprint.mission로 설정합니다.
    - [x] ArtifactId는 수정하지 않습니다.
    - [x] .gitignore에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 .idea디렉토리를 추가합니다.

#### 도메인 모델링
- [x] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
    - [x] 패키지명: com.sprint.mission.discodeit.entity
    - [x] 도메인 모델 정의
        - [x] 공통: id(UUID), createdAt/updatedAt(Long) 선언
        - [x] User, Channel, Message 클래스 구현
    - [x] 생성자 및 메소드 구현
        - [x] id, createdAt 생성자 초기화 및 나머지 필드 파라미터 초기화
        - [x] 각 필드 Getter 및 수정용 update 함수 정의

#### 서비스 설계 및 구현
- [x] 도메인 모델 별 CRUD 기능을 인터페이스로 선언하세요.
    - [x] 인터페이스 패키지명: com.sprint.mission.discodeit.service
- [x] 서비스 인터페이스의 구현체(JCF)를 작성하세요.
    - [x] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
    - [x] JCF를 활용하여 data 필드를 final로 선언 및 CRUD 메소드 구현

#### 메인 클래스 구현
- [x] JavaApplication 클래스에서 도메인 별 서비스 구현체를 테스트해보세요.
    - [x] 등록, 조회(단건/다건), 수정 및 확인, 삭제 및 확인 프로세스 완료

### 심화 요구 사항

#### 서비스 간 의존성 주입
- [x] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요.
    - 힌트: Message를 생성할 때 연관된 도메인 모델 데이터 확인하기


## 주요 변경사항
-


## 스크린샷
![image](이미지url)

## 멘토에게
- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.