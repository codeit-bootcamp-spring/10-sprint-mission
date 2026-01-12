📱DiscodeIt (Java Console App)  
DiscodeIt은 Discord의 핵심 기능을 모방하여 구현한 Java 기반의 백엔드 학습 프로젝트입니다.

데이터베이스 없이 Java Collection Framework(Map)를 활용하여 인메모리(In-Memory) 저장소를 구현하였으며,   
객체 지향 원칙(OOP)과 레이어드 아키텍처(Layered Architecture)를 기반으로 설계되었습니다.

🛠️ Tech Stack  
Language: Java 17+  
Storage: JCF (HashMap) - Memory DB Simulation

Architecture: Layered Architecture (Controller -> Service -> Repository/Memory)

📂 Project Structure

com.sprint.mission.discodeit  
├── entity            # 도메인 객체 (데이터 정의 및 기본 유효성 검사)  
│   ├── BaseEntity.java  (ID, 생성일시, 수정일시 공통 관리)  
│   ├── User.java  
│   ├── Channel.java  
│   └── Message.java  
├── service           # 비즈니스 로직 인터페이스  
│   ├── UserService.java  
│   ├── ChannelService.java  
│   └── MessageService.java  
├── service.jcf       # JCF(Map) 기반 서비스 구현체  
│   ├── JCFUserService.java  
│   ├── JCFChannelService.java  
│   └── JCFMessageService.java   
└── JavaApplication.java         # 애플리케이션 진입점 및 통합 테스트 시나리오  

✨ Key Features (핵심 기능)  
1. User (사용자)  
   사용자 등록 (ID 자동 생성 - UUID)  
사용자명 유효성 검사 (영문/한글/숫자, 2~20자 제한)  
사용자 정보 조회, 수정, 삭제
2. Channel (채널)  
   채널 생성 (생성 시 방장(Owner) 지정 필수)
채널명 중복 방지 로직  
채널 정보 수정 및 삭제
3. Message (메시지)  
   특정 채널에 메시지 전송
참조 무결성(Referential Integrity) 시뮬레이션:   
메시지 전송 시, 실제 존재하는 유저(User)와 채널(Channel)인지 검증
채널별 메시지 목록 조회 (작성 시간순 정렬)  

💡 Key Design Decisions (설계 의사결정)  
이 프로젝트를 진행하면서 고민했던 주요 설계 포인트입니다.
1. ID 참조 vs 객체 참조
   초기 설계: Channel 엔티티가 User 객체를 직접 필드로 가짐 (private User owner)
2. BaseEntity를 통한 중복 제거
   모든 엔티티가 공통으로 가지는 UUID id, createdAt, updatedAt 필드를 BaseEntity 추상 클래스로 분리하여 상속받도록 설계했습니다.   
   이를 통해 코드 중복을 줄이고 일관성을 확보했습니다.  
3. 인터페이스(Interface) 기반 설계  
   UserService 등의 로직을 인터페이스로 먼저 정의하고, 구현체(JCFUserService)를 따로 만들었습니다.

이유: 나중에 인메모리(Map) 방식이 아니라 실제 DB(MySQL 등)를 사용하는 구현체로 갈아끼울 때, 기존 코드를 수정하지 않고 확장하기 위함입니다.

✅ Test Scenarios  
Main.java를 통해 다음과 같은 통합 테스트 시나리오를 수행합니다.  
User: 정상 생성 확인, 이름 유효성 검사 실패 확인 (경계값 테스트)  
Channel: 유저 ID를 이용한 채널 생성, 채널명 수정 확인  
Message: 유저와 채널 ID를 이용한 메시지 전송, 존재하지 않는 유저로 전송 시 예외 발생 확인(방어 로직)  
Flow: 등록 -> 조회 -> 수정 -> 삭제 -> 삭제 확인 (Exception 발생)  

📝 TODO / Future Improvements   
[ ] ChannelMember 엔티티 분리를 통한 N:M 관계(참여자 목록) 구현  
[ ] 예외 처리(Exception) 커스텀 클래스 정의  