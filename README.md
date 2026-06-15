# 10-spring-mission
Discode clone coding

---
[![codecov](https://codecov.io/github/metDaisy/sprint-mission/graph/badge.svg?token=2QWY52ZBXP)](https://codecov.io/github/metDaisy/sprint-mission)

---

- mission-10
  - spring security with jwt

- mission-9
  - spring security
    - authentication, authorization

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
      - ./.storage/db-data:/var/lib/postgresql/data
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
      - ./.storage/local-uploads:/app/${LOCAL_STORAGE_ROOT_PATH}
    environment:
      JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

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

MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=1024MB

SPRING_PROFILES_ACTIVE=dev

STORAGE_TYPE=local # local | s3
LOCAL_STORAGE_ROOT_PATH=.discodeit/storage
LOCAL_STORAGE_INTERNAL_PATH=/internal-local
S3_STORAGE_INTERNAL_PATH=/internal-s3

AWS_S3_ACCESS_KEY=
AWS_S3_SECRET_KEY=
AWS_S3_BUCKET=
AWS_S3_REGION=ap-northeast-2
AWS_S3_PRESIGNED_URL_EXPIRATION=600

ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@admin.com
ADMIN_PASSWORD=admin

JWT_SECRET_KEY=ifrtL3sSclJZ7r/3tr9zOXxQAU3vMzW7rZYqO6oI4nR3EeuYnkTt+wmT468ZKEevfyK+cDg9QtFsLRQ5F0VWtA==
JWT_ACCESS_TOKEN_EXPIRATION=900
JWT_REFRESH_TOKEN_EXPIRATION=1209600 # 14d
JWT_REGISTRY_STORE_TYPE=in-memory # in-memory | db
JWT_REGISTRY_MAX_CONCURRENT=1
JWT_REGISTRY_MAX_RETAINED=5

SPRING_CACHE_TYPE=redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=discodeit1234

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
