# 10-spring-mission

- mission-5
  - 주어진 api specification 을 보고 다시 구현하기
  - 배포 해보기

# 구현

- 기능별로 package 를 구성했다.
- `controller`
  - `UserController`
  - `AuthController`
  - `ChannelController`
  - `MessageController`
- `service`
    - `AuthService`
    - `BinaryContentService`
    - `DomainService`
    - `UserService`
    - `ChannelService`
    - `MessageService`
    - `ReadStatusService`
    - `UserStatusService`
- `repository`
    - `DomainRepository`
    - `UserRepository`
    - `ChannelRepository`
    - `MessageRepository`
    - `UserStatusRepository`
    - `ReadStatusRepository`
    - `BinaryContentRepository`
- `entity`
    - `User`
    - `UserStatus`
    - `Channel`
    - `ChannelType`
    - `ReadStatus`
    - `ReadType`
    - `Message`
    - `BinaryContent`
- `dto`
    - `UserServiceDTO`
    - `AuthServiceDTO`
    - `BinaryContentServiceDTO`
    - `ChannelServiceDTO`
    - `MessageServiceDTO`
    - `ReadStatusServiceDTO`
    - `UserStatusServiceDTO`
- `test`
  - `controller`
    - `BaseControllerTest`
    - `UserControllerTest`
  - `fixture`
    - `UserFixture`
    - `BinaryContentFixture`

### Todo
- toJson
- encryption, decryption