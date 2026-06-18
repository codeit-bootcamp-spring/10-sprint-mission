package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.user.UserLogInOutEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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

/// InMemoryJwtRegistry 대신 Redis 저장소를 사용하는 구현체.
@Slf4j
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

    /// 사용자별 JWT 목록
    /**
     jwt:user:123
     [
     JwtInformation(accessToken=A1, refreshToken=R1),
     JwtInformation(accessToken=A2, refreshToken=R2)
     ]
     **/
    private static final String USER_JWT_KEY_PREFIX = "jwt:user:";

    /// Access Token 인덱스: access token  유효성 확인을 빠르게 하기 위해서
    /// hasActiveJwtInformationByAccessToken(String accessToken)으로 전체 사용자 목록 뒤지지 않고
    /// redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)으로 한번에 확인 가능.
    private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";

    /// Refresh Token 인덱스
    private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockProvider redisLockProvider;


    /** 로그인 성공 후 호출되는 메서드
     (1) 로그인 성공
     (2) JWT 생성
     (3) registerJwtInformation()
     (4) 사용자별 lock 획득
     (5) 기존 활성 토큰 개수 확인
     (6) maxActiveJwtCount 초과시 가장 오래된 토큰 제거
     (7) 새 JwtInformation Redis List에 저장
     (8) access/refresh token index Set에 추가
     (9) TTL 설정
     (10) lock 해제
     (11) 로그인 이벤트 발생
     **/

    @CacheEvict(value = "users", key = "'all'")
    @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UserDto user = jwtInformation.getUserDto();
        String userKey = getUserKey(jwtInformation.getUserDto().id());
        String lockKey = jwtInformation.getUserDto().id().toString();

        redisLockProvider.acquireLock(lockKey);
        try {
            /// 현재 사용자의 활성 토크 개수 확인.
            Long currentSize = redisTemplate.opsForList().size(userKey);

            /// 최대 개수 넘으면 가장 오래된 토큰 제거
            while (currentSize != null && currentSize >= maxActiveJwtCount) {
                Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
                if (oldestTokenObj instanceof JwtInformation oldestToken) {
                    removeTokenIndex(oldestToken.getAccessToken(), oldestToken.getRefreshToken());
                }
                currentSize = redisTemplate.opsForList().size(userKey);
            }

            /// 새 토큰 추가.
            redisTemplate.opsForList().rightPush(userKey, jwtInformation);
            redisTemplate.expire(userKey, DEFAULT_TTL);
            /// 빠른 조회용 Set에도 추가.
            addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

        } finally {
            redisLockProvider.releaseLock(lockKey);
        }

        UserDto onlineUser = new UserDto(
                user.id(),
                user.username(),
                user.email(),
                user.profile(),
                true,
                user.role()
        );

        eventPublisher.publishEvent(
                new UserLogInOutEvent(onlineUser)
        );

    }

    /** 로그아웃 또는 강제 만료
     (1) 사용자 로그아웃
     (2) jwt:user:{userId} 목록 조회
     (3) 각 토큰을 access/refresh 인덱스 Set에서 제거
     (4) 사용자 JWT List 삭제
     (5) 로그아웃 이벤트 발생

     -> 해당 사용자의 모든 활성 토큰을 무효화
     **/
    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);

        List<Object> tokens =
                redisTemplate.opsForList().range(userKey, 0, -1);

        UserDto user = null;

        if (tokens != null) {
            for (Object tokenObj : tokens) {
                if (tokenObj instanceof JwtInformation jwtInfo) {
                    if (user == null) {
                        user = jwtInfo.getUserDto();
                    }

                    removeTokenIndex(
                            jwtInfo.getAccessToken(),
                            jwtInfo.getRefreshToken()
                    );
                }
            }
        }
        redisTemplate.delete(userKey);

        if (user != null) {
            UserDto offlineUser = new UserDto(
                    user.id(),
                    user.username(),
                    user.email(),
                    user.profile(),
                    false,
                    user.role()
            );
            eventPublisher.publishEvent(
                    new UserLogInOutEvent(offlineUser)
            );
        }
    }

    /// 사용자가 활성 JWT를 가지고 있는지 확인
    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        String userKey = getUserKey(userId);
        Long size = redisTemplate.opsForList().size(userKey);
        return size != null && size > 0;
    }

    /// Access Token이 현재 활성 상태인지 확인
    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
        );
    }

    /// Refresh Token 활성여부 확인
    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
        );
    }

    /** refresh token으로 새 JWT 재발급할때 사용
     (1) refresh 요청
     (2) refresh token 검증
     (3) rotateJwtInformation()
     (4) 사용자별 lock 획득
     (5) jwt:user:{userId} 목록 조회
     (6) 기존 refreshToken과 일치하는 JwtInformation 찾기
     (7) 기존 access/refresh token 인덱스 제거
     (8) 새 access/refresh token으로 교체
     (9) 새 토큰 인덱스 추가
     (10) TTL 갱신
     (11) lock 해제

     -> 기존 refresh token 재사용 방지
     -> refresh 요청 후에는 이전 refresh token은 더이상 활성 인덱스에 없게 된다.
     **/
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

    /// 5분마다 만료 토큰 정리
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
