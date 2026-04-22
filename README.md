# 10-spring-mission
discode clone coding

---

- mission-8
  - Dockerfile
  - docker-compose
  - aws s3

<details>
<summary>docker-compose.override.yml</summary>

```yaml
services:
  db:
    image: postgres:17-alpine
    restart: always
    environment:
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: ${DB_NAME}
    ports:
      - "5432:5432"
    volumes:
      - ./db-data:/var/lib/postgresql/data
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}" ]
      interval: 5s
      timeout: 5s
      retries: 5

  app:
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "5005:5005"
    volumes:
      - local-uploads:/app/${LOCAL_STORAGE_ROOT_PATH}
    environment:
      JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    healthcheck:
      test: [ "CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1" ]
      interval: 10s
      timeout: 5s
      retries: 6

```

</details>

<details>
<summary>.env</summary>

```dotenv
DB_HOST=db
DB_PORT=5432
DB_USER=discodeit_user
DB_PASSWORD=discodeit1234
DB_NAME=discodeit

SPRING_PROFILES_ACTIVE=dev

STORAGE_TYPE=local # local | s3
LOCAL_STORAGE_ROOT_PATH=.discodeit/storage

AWS_S3_ACCESS_KEY=
AWS_S3_SECRET_KEY=
AWS_S3_BUCKET=
AWS_S3_REGION=
AWS_S3_PRESIGNED_URL_EXPIRATION=

```

</details>

---

- mission-7
  - custom exception
  - logging
  - entity
    - unit test
    - ex) UserStatus.isOnline()
  - repository
    - slice test
    - custom queries
  - service
    - unit test
    - business logics
  - controller
    - slice test
    - custom validator test
  - controller ~ repository
    - integrated test(SpringBootTest)

---

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
