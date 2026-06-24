### 실행 명령어 정리
Docker Compose 실행
```shell
mkdir -p logs && docker-compose --env-file .env up --build 2>&1 | tee logs/build-$(date +%Y%m%d_%H%M%S).log
```

app 로그만 실시간으로 보면서 파일로도 저장
```shell 
docker compose up -d --build 
docker compose ps    
docker compose logs -f app | tee logs/app-$(date +%Y%m%d_%H%M%S).log
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

# [SB] 스프린트 미션 12

## 🏔️ 프로젝트 마일스톤

- 웹소켓과 SSE를 활용한 실시간 통신
- Nginx를 활용한 배포 아키텍처 구성

### 주요 변경 사항

1. 프로젝트 버전이 변경되었습니다. `v3.0-M12`

- 세부 사항

```markdown
  # build.gradle
  ...
-  version = '2.3-M11'
+ version = '3.0-M12'
  ...
```

- `3.0`: `api-doc` 버전을 따릅니다.
- `M12`: 미션 12을 의미합니다.

2. 프론트엔드가 변경되었습니다.

- `v3.0.0`
    - 폴링이 삭제되고, 웹소켓, SSE 기능이 추가되었습니다.
    - 정적 리소스: 베이스 코드에 적용되어 있습니다.
    - 소스 코드 (참고용)

> 프론트엔드 소스 코드는 참고용으로만 활용하세요. 수정하여 활용하는 경우 이어지는 요구사항 또는 미션을 수행하는 데 어려움이 있을 수 있습니다.

## 📝 요구사항

### ✏️ 기본 요구사항

### 01. 웹소켓 구현하기

### 웹소켓 구현하기

- [ ] 웹소켓 환경 구성
    - `spring-boot-starter-websocket` 의존성을 추가하세요.

      ```groovy
      implementation 'org.springframework.boot:spring-boot-starter-websocket'
      ```

    - 웹소켓 메시지 브로커 설정

      ```java
      @Configuration
      @EnableWebSocketMessageBroker
      public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
          ...
      }
      ```

        - 메모리 기반 `SimpleBroker`를 사용하세요.

          ```java
          @Override
          public void configureMessageBroker(MessageBrokerRegistry config) {
              ...
          }
          ```

            - SimpleBroker의 Destination Prefix는`/sub`으로 설정하세요.
                - 클라이언트에서 메시지를 구독할 때 사용합니다.
            - Application Destination Prefix는`/pub`으로 설정하세요.
                - 클라이언트에서 메시지를 발행할 때 사용합니다.

          ```java
          @Override
          public void registerStompEndpoints(StompEndpointRegistry registry) {
              ...
          }
          ```

            - STOMP 엔드포인트는`/ws`로 설정하고,`SockJS`연결을 지원해야 합니다.
- [ ] 메시지 송신
    - 첨부파일이 없는 단순 텍스트 메시지인 경우 STOMP를 통해 메시지를 전송할 수 있도록 컨트롤러를 구현하세요.

      ```java
      @Controller
      public class MessageWebSocketController {
          ...
          @MessageMapping(...)
      }
      ```

        - 클라이언트는 웹소켓으로`/pub/messages`엔드포인트에 메시지를 전송할 수 있어야 합니다.
            - `@MessageMapping`을 활용하세요.
        - 메시지 전송 요청의 페이로드 타입은`MessageCreateRequest`를 그대로 활용합니다.
    - 첨부파일이 포함된 메시지는 기존의 API (`POST /api/messages`)를 그대로 활용합니다.

- [ ] 메시지 수신
    - 클라이언트는 채널 입장 시 웹소켓으로 `/sub/channels.{channelId}.messages` 를 구독해 메시지를 수신합니다.

    - 이를 고려해 메시지가 생성되면 해당 엔드포인트로 메시지를 보내는 컴포넌트를 구현하세요.

      ```java
      @Component
      public class WebSocketRequiredEventListener {
          ...
          private final SimpMessagingTemplate messagingTemplate;

          @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
          public void handleMessage(MessageCreatedEvent event) {
              ...
          }
      }
      ```

        - `MessageCreatedEvent`를 통해 새로운 메시지 생성 이벤트를 확인하세요.
        - `SimpMessagingTemplate`를 통해 적절한 엔드포인트로 메시지를 전송하세요.

### 02. SSE 구현하기

- [ ] SSE 환경을 구성하세요.

    - 클라이언트에서 SSE 연결을 위한 엔드포인트를 구현하세요.

        - `GET /api/sse`
    - 사용자별 SseEmitter 객체를 생성하고 메시지를 전송하는 컴포넌트를 구현하세요.

      ```java
      @Service
      public class SseService {
          public SseEmitter connect(UUID receiverId, UUID lastEventId) { ... }
          public void send(Collection<UUID> receiverIds, String eventName, Object data) { ... }
          public void broadcast(String eventName, Object data) { ... }
          @Scheduled(fixedDelay = 1000 * 60 * 30)
          public void cleanUp() { ... }
          private boolean ping(SseEmitter sseEmitter) { ... }
      }
      ```

        - `connect`: SseEmitter 객체를 생성합니다.
        - `send`,`broadcast`: SseEmitter 객체를 통해 이벤트를 전송합니다.
        - `cleanUp`: 주기적으로 ping을 보내서 만료된`SseEmitter`객체를 삭제합니다.
        - `ping`: 최초 연결 또는 만료 여부를 확인하기 위한 용도로 더미 이벤트를 보냅니다.
    - `SseEmitter` 객체를 메모리에서 저장하는 컴포넌트를 구현하세요.

      ```java
      @Repository
      public class SseEmitterRepository {
          private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();
          ...
      }
      ```

        - `ConcurrentMap`: 스레드 세이프한 자료구조를 사용합니다.
        - `List<SseEmitter>`: 사용자 당 N개의 연결을 허용할 수 있도록 합니다. (예: 다중 탭)
    - 이벤트 유실 복원을 위해 SSE 메시지를 저장하는 컴포넌트를 구현하세요.

      ```java
      @Repository
      public class SseMessageRepository {
          private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
          private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
          ...
      }
      ```

        - 각 메시지 별로 고유한 ID를 부여합니다.
        - 클라이언트에서`LastEventId`를 전송해 이벤트 유실 복원이 가능하도록 해야 합니다.
- [ ] 기존에 클라이언트에서 폴링 방식으로 주기적으로 요청하던 데이터를 SSE를 이용해 서버에서 실시간으로 전달하는 방식으로 리팩토링하세요.

    - 새로운 알림 이벤트 전송

        - 새 알림이 생성되었을 때 클라이언트에 이벤트를 전송하세요.

        - 클라이언트는 이 이벤트를 수신하면 알림 목록에 알림을 추가합니다.

        - 이벤트 명세

          |id|이벤트 고유 ID|
                    |---|---|
          |name|`notifications.created`|
          |data|`NotificationDto`|

    - 파일 업로드 상태 변경 이벤트 전송

        - 파일 업로드 상태가 변경될 때 이벤트를 발송하세요.

        - 클라이언트는 해당 상태를 수신하면 파일 상태 UI를 다시 렌더링합니다.

        - 이벤트 명세

          |id|이벤트 고유 ID|
                    |---|---|
          |name|`binaryContents.updated`|
          |data|`BinaryContentDto`|

    - 채널 갱신 이벤트 전송

        - 채널 정보가 변경될 때, 이벤트를 발송하세요.

        - 클라이언트는 해당 이벤트를 수신하면 채널 UI를 다시 렌더링합니다.

        - 이벤트 명세

          |id|이벤트 고유 ID|
                    |---|---|
          |name|`channels.created` or `updated` or `deleted`|
          |data|`ChannelDto`|

    - 사용자 갱신 이벤트 전송

        - 사용자 정보 또는 로그인 상태가 변경될 때, 이벤트를 발송하세요.

        - 클라이언트는 해당 이벤트를 수신하면 사용자 UI를 다시 렌더링합니다.

        - 이벤트 명세

          |id|이벤트 고유 ID|
                    |---|---|
          |name|`users.created` or `updated` or `deleted`|
          |data|`UserDto`|

### 03. 배포 아키텍처 구성하기

- [ ] 다음의 다이어그램에 부합하는 배포 아키텍처를 Docker Compose를 통해 구현하세요.

  ![gtwxscalk-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14132&version=1&directory=/gtwxscalk-image.png&name=gtwxscalk-image.png)

    - `Reverse Proxy`
        - Nginx 기반의 리버스 프록시 컨테이너를 구성하세요.
        - 역할 및 설정은 다음과 같습니다:
            - `/api/*`,`/ws/*`요청은**Backend 컨테이너**로 프록시 처리합니다.
            - 이 외의 모든 요청은**정적 리소스(프론트엔드 빌드 결과)**를 서빙합니다.
                - 프론트엔드 정적 리소스는 Nginx 컨테이너 내부의 적절한 경로(`/usr/share/nginx/html`등)에 복사하세요.
        - 외부에서 접근 가능한 유일한 컨테이너이며,`3000`번 포트를 통해 접근할 수 있어야 합니다.
    - `Backend`
        - Spring Boot 기반의 백엔드 서버를 Docker 컨테이너로 구성하세요.
        - `Reverse Proxy`를 통해`/api/*`,`/ws/*`요청이 이 서버로 전달됩니다.
    - `DB`,`Memory DB`,`Message Broker`
        - `Backend`컨테이너가 접근 가능한 다음의 인프라 컨테이너들을 구성하세요
            - **DB**: PostgreSQL
            - **Memory DB**: Redis
            - **Message Broker**: Kafka
        - 각 컨테이너는 Docker Compose 네트워크를 통해 백엔드에서 통신할 수 있어야 합니다.
        - 외부 네트워크와 단절되어야 합니다.

### ✏️ 심화 요구사항

### 01. 웹소켓 인증/인가 처리하기

- [ ] 인증 처리

    - 디스코드잇 클라이언트는 `CONNECT` 프레임의 헤더에 다음과 같이 `Authorization` 토큰을 포함합니다.

      ```markdown
      CONNECT 
      Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3b29keSIsImV4cCI6MTc0OTM5MzA0OCwiaWF0IjoxNzQ5MzkyNDQ4LCJ1c2VyRHRvIjp7ImlkIjoiMDQwZTk2ZWMtMjdmNC00Y2MxLWI4MWQtNTMyM2ExZWQ5NTZhIiwidXNlcm5hbWUiOiJ3b29keSIsImVtYWlsIjoid29vZHlAZGlzY29kZWl0LmNvbSIsInByb2ZpbGUiOm51bGwsIm9ubGluZSI6bnVsbCwicm9sZSI6IlVTRVIifX0.JOkvCpnR0e0KMQYLh_hUWglgTvUIlfQOT58eD4Cym5o 
      accept-version:1.2,1.1,1.0 
      heart-beat:4000,4000
      ```

    - 서버 측에서는 `ChannelInterceptor`를 구현하여 연결 시 토큰을 검증하고, 인증된 사용자 정보를 `SecurityContext`에 설정해야 합니다.

      > 참고
      문서: [Spring 공식 문서](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/authentication-token-based.html)

      ```java
      @Configuration
      @EnableWebSocketMessageBroker
      @RequiredArgsConstructor
      public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
          ...
          @Override
          public void configureClientInboundChannel(ChannelRegistration registration) {
              registration.interceptors(...);
          }
      }
      ```

    - `CONNECT` 프레임일 때 엑세스 토큰을 검증하는 `JwtAuthenticationChannelInterceptor` 구현체를 정의하세요.

      ```java
      public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {
              StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
              if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                  ... // 검증 로직
                  UsernamePasswordAuthenticationToken authentication = ...
                  accessor.setUser(authentication);
              }
              return message;
          }
      }
      ```

        - 검증 로직은 이전에 구현한`JwtAuthenticationFilter`를 참고하세요.
        - 인증이 완료되면`SecurityContext`에 인증정보를 저장하는 대신`accessor`객체에 저장하세요.
    - `SecurityContextChannelInterceptor`를 등록하여 이후 메시지 처리 흐름에서도 인증 정보를 활용할 수 있도록 구성하세요.

      ```java
      @Override
      public void configureClientInboundChannel(ChannelRegistration registration) {
          registration.interceptors(
              jwtAuthenticationChannelInterceptor,
              new SecurityContextChannelInterceptor(),
          );
      }
      ```

- [ ] 인가 처리

    - `AuthorizationChannelInterceptor`를 사용해 메시지 권한 검사를 수행합니다.

    - `AuthorizationChannelInterceptor`를 활용하기 위해의존성을 추가하세요.

      ```groovy
      implementation 'org.springframework.security:spring-security-messaging'
      ```

    - `MessageMatcherDelegatingAuthorizationManager`를 활용해 인가 정책을 정의하고, 채널에 추가하세요.

      ```java
      private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
          return new AuthorizationChannelInterceptor(
              MessageMatcherDelegatingAuthorizationManager.builder()
                  .anyMessage().hasRole(Role.USER.name())
                  .build()
          );
      }
      ```

      ```java
      @Override
      public void configureClientInboundChannel(ChannelRegistration registration) {
          registration.interceptors(
              jwtAuthenticationChannelInterceptor,
              new SecurityContextChannelInterceptor(),
              authorizationChannelInterceptor()
          );
      }
      ```

### 02. 분산 환경 배포 아키텍처 구성하기

- [ ] 다음의 다이어그램에 부합하는 배포 아키텍처를 Docker Compose를 통해 구현하세요.

  ![zkdz2mts7-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14134&version=1&directory=/zkdz2mts7-image.png&name=zkdz2mts7-image.png)

    - `Backend-*`
        - `deploy.replicas`설정을 활용하세요.
    - `Reverse Proxy`
        - `upstream` 블록을 수정해 다음의 로드밸런싱 전략을 적용해 `Backend`로 트래픽을 분산시켜보세요.

            - **Round Robin**`기본값`
            - **Least Connections**
            - **IP Hash**
            - **Weight**
        - `$upstream_addr` 변수를 활용해 실제 요청을 처리하는 서버의 IP를 헤더에 추가하고 브라우저 개발자 도구를 활용해 비교해보세요.

          ```nginx
          location ^~ /api/sse {
              ...
              add_header X-Upstream-Server $upstream_addr;
          }

          location ^~ /api/ {
              ...
              add_header X-Upstream-Server $upstream_addr;
          }

          location ^~ /ws/ {
              ...
              add_header X-Upstream-Server $upstream_addr;
          }
          ```

- [ ] 분산환경에 따른 `InMemoryJwtRegistry`의 한계점을 식별하고 Redis를 활용해 리팩토링하세요.

    - 어떤 한계가 있는지 식별하고 PR에 남겨주세요.

    - `RedisJwtRegistry` 구현체를 활용하세요.

      ```java
      @Configuration
      public class RedisConfig {

          @Bean
          public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
              @Qualifier("redisSerializer") GenericJackson2JsonRedisSerializer redisSerializer) {
              RedisTemplate<String, Object> template = new RedisTemplate<>();
              template.setConnectionFactory(connectionFactory);

              // Use String serializer for keys
              template.setKeySerializer(new StringRedisSerializer());
              template.setHashKeySerializer(new StringRedisSerializer());

              // Use JSON serializer for values
              template.setValueSerializer(redisSerializer);
              template.setHashValueSerializer(redisSerializer);

              template.afterPropertiesSet();
              return template;
          }

          @Bean("redisSerializer")
          public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper objectMapper) {
              ObjectMapper redisObjectMapper = objectMapper.copy();
              redisObjectMapper.activateDefaultTyping(
                  LaissezFaireSubTypeValidator.instance,
                  DefaultTyping.EVERYTHING,
                  As.PROPERTY
              );
              return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
          }
      }
      ```

      ```java
      package com.sprint.mission.discodeit.redis;

      import java.time.Duration;
      import lombok.RequiredArgsConstructor;
      import lombok.extern.slf4j.Slf4j;
      import org.springframework.data.redis.core.RedisTemplate;
      import org.springframework.data.redis.core.ValueOperations;
      import org.springframework.stereotype.Component;

      @Slf4j
      @RequiredArgsConstructor
      @Component
      public class RedisLockProvider {

          private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
          private static final String LOCK_KEY_PREFIX = "lock:";

          private final RedisTemplate<String, Object> redisTemplate;

          public void acquireLock(String key) {
              String lockKey = LOCK_KEY_PREFIX + key;
              String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
              ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

              // SETNX: 키가 없으면 설정하고 TTL 지정
              Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

              if (Boolean.TRUE.equals(acquired)) {
                  log.debug("분산 락 획득 성공: {} (값: {})", lockKey, lockValue);
              } else {
                  log.debug("분산 락 획득 실패: {}", lockKey);
                  throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
              }
          }

          public void releaseLock(String key) {
              String lockKey = LOCK_KEY_PREFIX + key;
              try {
                  redisTemplate.delete(lockKey);
                  log.debug("분산 락 해제 완료: {}", lockKey);
              } catch (Exception e) {
                  log.warn("분산 락 해제 실패: {}", lockKey, e);
              }
          }

          public static class RedisLockAcquisitionException extends RuntimeException {

              public RedisLockAcquisitionException(String message) {
                  super(message);
              }
          }
      }
      ```

        - 원자적 연산을 위해 분산락을 사용합니다.

      ```java
      package com.sprint.mission.discodeit.security.jwt;

      import com.sprint.mission.discodeit.dto.data.JwtInformation;
      import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
      import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;
      import com.sprint.mission.discodeit.redis.RedisLockProvider;
      import java.time.Duration;
      import java.util.List;
      import java.util.Set;
      import java.util.UUID;
      import lombok.RequiredArgsConstructor;
      import lombok.extern.slf4j.Slf4j;
      import org.springframework.cache.annotation.CacheEvict;
      import org.springframework.context.ApplicationEventPublisher;
      import org.springframework.data.redis.core.RedisTemplate;
      import org.springframework.retry.annotation.Backoff;
      import org.springframework.retry.annotation.Retryable;
      import org.springframework.scheduling.annotation.Scheduled;

      @Slf4j
      @RequiredArgsConstructor
      public class RedisJwtRegistry implements JwtRegistry {

          private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
          private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
          private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
          private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

          private final int maxActiveJwtCount;
          private final JwtTokenProvider jwtTokenProvider;
          private final ApplicationEventPublisher eventPublisher;
          private final RedisTemplate<String, Object> redisTemplate;
          private final RedisLockProvider redisLockProvider;

          @CacheEvict(value = "users", key = "'all'")
          @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
              backoff = @Backoff(delay = 100, multiplier = 2))
          @Override
          public void registerJwtInformation(JwtInformation jwtInformation) {
              String userKey = getUserKey(jwtInformation.getUserDto().id());
              String lockKey = jwtInformation.getUserDto().id().toString();

              redisLockProvider.acquireLock(lockKey);
              try {
                  Long currentSize = redisTemplate.opsForList().size(userKey);

                  while (currentSize != null && currentSize >= maxActiveJwtCount) {
                      Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
                      if (oldestTokenObj instanceof JwtInformation oldestToken) {
                          removeTokenIndex(oldestToken.getAccessToken(), oldestToken.getRefreshToken());
                      }
                      currentSize = redisTemplate.opsForList().size(userKey);
                  }

                  redisTemplate.opsForList().rightPush(userKey, jwtInformation);
                  redisTemplate.expire(userKey, DEFAULT_TTL);
                  addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

              } finally {
                  redisLockProvider.releaseLock(lockKey);
              }

              eventPublisher.publishEvent(
                  new UserLogInOutEvent(jwtInformation.getUserDto().id(), true)
              );
          }

          @CacheEvict(value = "users", key = "'all'")
          @Override
          public void invalidateJwtInformationByUserId(UUID userId) {
              String userKey = getUserKey(userId);

              List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
              if (tokens != null) {
                  tokens.forEach(tokenObj -> {
                      if (tokenObj instanceof JwtInformation jwtInfo) {
                          removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
                      }
                  });
              }

              redisTemplate.delete(userKey);
              eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
          }

          @Override
          public boolean hasActiveJwtInformationByUserId(UUID userId) {
              String userKey = getUserKey(userId);
              Long size = redisTemplate.opsForList().size(userKey);
              return size != null && size > 0;
          }

          @Override
          public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
              return Boolean.TRUE.equals(
                  redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
              );
          }

          @Override
          public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
              return Boolean.TRUE.equals(
                  redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
              );
          }

          @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
              backoff = @Backoff(delay = 100, multiplier = 2))
          @Override
          public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
              String userKey = getUserKey(newJwtInformation.getUserDto().id());
              String lockKey = newJwtInformation.getUserDto().id().toString();

              redisLockProvider.acquireLock(lockKey);
              try {
                  List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

                  if (tokens != null) {
                      for (int i = 0; i < tokens.size(); i++) {
                          if (tokens.get(i) instanceof JwtInformation jwtInfo &&
                              jwtInfo.getRefreshToken().equals(refreshToken)) {

                              removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
                              jwtInfo.rotate(newJwtInformation.getAccessToken(),
                                  newJwtInformation.getRefreshToken());
                              redisTemplate.opsForList().set(userKey, i, jwtInfo);
                              addTokenIndex(newJwtInformation.getAccessToken(),
                                  newJwtInformation.getRefreshToken());
                              redisTemplate.expire(userKey, DEFAULT_TTL);
                              break;
                          }
                      }
                  }

              } finally {
                  redisLockProvider.releaseLock(lockKey);
              }
          }

          @Scheduled(fixedDelay = 1000 * 60 * 5)
          @Override
          public void clearExpiredJwtInformation() {
              Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");

              for (String userKey : userKeys) {
                  List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

                  if (tokens != null) {
                      boolean hasValidTokens = false;

                      for (int i = tokens.size() - 1; i >= 0; i--) {
                          if (tokens.get(i) instanceof JwtInformation jwtInfo) {
                              boolean isExpired =
                                  !jwtTokenProvider.validateAccessToken(jwtInfo.getAccessToken()) ||
                                      !jwtTokenProvider.validateRefreshToken(jwtInfo.getRefreshToken());

                              if (isExpired) {
                                  redisTemplate.opsForList().set(userKey, i, "EXPIRED");
                                  redisTemplate.opsForList().remove(userKey, 1, "EXPIRED");
                                  removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
                              } else {
                                  hasValidTokens = true;
                              }
                          }
                      }

                      if (!hasValidTokens) {
                          redisTemplate.delete(userKey);
                      }
                  }
              }
          }

          private String getUserKey(UUID userId) {
              return USER_JWT_KEY_PREFIX + userId.toString();
          }

          private void addTokenIndex(String accessToken, String refreshToken) {
              // Set에 토큰 추가 (add: 중복되면 무시됨)
              redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, accessToken);
              redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refreshToken);

              // 인덱스 키에도 만료 시간 설정 (메모리 누수 방지)
              redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, DEFAULT_TTL);
              redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, DEFAULT_TTL);
          }

          private void removeTokenIndex(String accessToken, String refreshToken) {
              // Set에서 토큰 제거
              redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, accessToken);
              redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
          }
      }
      ```


- [ ] 분산환경에 따른 웹소켓과 SSE의 한계점을 식별하고 Kafka를 활용해 리팩토링하세요.
    - 어떤 한계가 있는지 식별하고 PR에 남겨주세요.
    - 일반적인 카프카 이벤트와 다르게 각 서버 인스턴스마다 이벤트를 받을 수 있어야 합니다. 따라서`컨슈머 group id`를 적절히 설정하세요.

## 🔄 주요 변경사항

## 📸 스크린샷

## 🙇🏽‍♂️ 멘토에게
