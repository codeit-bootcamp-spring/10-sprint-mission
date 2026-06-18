package com.sprint.mission.discodeit.security.jwt;


import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;
import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_JWT_KEY_PREFIX = "jwt:user:"; // jwt key 접두사
  private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens"; // 액세스 토큰 접두사
  private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens"; // 리프레시 토큰 접두사
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(30); // TTL은 30분

  @Value("${discodeit.jwt.max-active-count:1}") // 최대 활성화된 JWT 토큰 개수는 1
  private int maxActiveJwtCount;

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisLockProvider redisLockProvider;

  // Jwt 정보를 레지스트리에 담는 메서드
  @CacheEvict(value = "users", key = "'all'")
  @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    String userKey = getUserKey(jwtInformation.getUserDto().id()); // Redis에 저장할 키를 만듬
    String lockKey = jwtInformation.getUserDto().id().toString(); // 분산 락에 사용할 키를 만듬

    redisLockProvider.acquireLock(lockKey); // redis 분산 락을 획득. 이 시점부터 하나의 서버만 jwt 등록 작업을 수행
    try {
      Long currentSize = redisTemplate.opsForList().size(userKey); // redis에 저장된 이 사용자의 JWT 개수를 확인

      while (currentSize != null && currentSize >= maxActiveJwtCount) { // 개수가 max 보다 많으면 (1 이상)
        Object oldestTokenObj = redisTemplate.opsForList()
            .leftPop(userKey); // Redis list에서 가장 오래된 jwt를 삭제
        if (oldestTokenObj instanceof JwtInformation oldestToken) {
          removeTokenIndex(oldestToken.getAccessToken(),
              oldestToken.getRefreshToken()); // 인덱스 set에서도 제거
        }
        currentSize = redisTemplate.opsForList()
            .size(userKey); // 현재 사이즈를 업데이트. maxActive 보다 작아질 때 까지 반복
      }

      redisTemplate.opsForList().rightPush(userKey, jwtInformation); //
      redisTemplate.expire(userKey, DEFAULT_TTL);
      addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
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

  @Override
  public void removeJwtInformationByUserId(UUID userId) {
    invalidateJwtInformationByUserId(userId);
  }

  @Override
  public void removeJwtInformationByRefreshToken(String refreshToken) {
    Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (userKeys == null) {
      return;
    }

    for (String userKey : userKeys) {
      List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
      if (tokens == null) {
        continue;
      }

      for (int i = tokens.size() - 1; i >= 0; i--) {
        if (tokens.get(i) instanceof JwtInformation jwtInfo
            && refreshToken.equals(jwtInfo.getRefreshToken())) {
          removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
          redisTemplate.opsForList().set(userKey, i, "REMOVED");
          redisTemplate.opsForList().remove(userKey, 0, "REMOVED");
        }
      }

      Long size = redisTemplate.opsForList().size(userKey);
      if (size == null || size == 0) {
        redisTemplate.delete(userKey);
      }
    }
  }

  // UUID 형식의 유저 아이디를 받아 jwt 접두어가 붙어진 String 형식의 UserKey를 반환한다.
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
