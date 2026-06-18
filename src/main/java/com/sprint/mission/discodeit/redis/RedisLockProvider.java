package com.sprint.mission.discodeit.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import java.time.Duration;

/// 분산 락을 담당하는 클래스
/// backend가 3개인경우 동시에 같은 사용자의 JWT목록을 수정할 수 있기 때문에 필요.

/**
 backend-1: userA 로그인
 backend-2: userA refresh
 backend-3: userA 로그인
 -> 이 요청들이 동시에 오면 같은 Reids key를 수정하게 된다.
 jwt: user: userA
 그래서 동시 수정 충돌을 막기 위해 lock을 건다.
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockProvider {

    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final String LOCK_KEY_PREFIX = "lock:";

    private final RedisTemplate<String, Object> redisTemplate;

    /// 락 휙득
    public void acquireLock(String key) {
        /// ex: lock:550e8400-e29b-41d4-a716-446655440000
        String lockKey = LOCK_KEY_PREFIX + key;
        String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        // SETNX: 키가 없으면 설정하고 TTL 지정
        /// key가 없으면 저장하고 true, 이미 있으면 false
        /// 즉, lock: userA 없다. -> backend-1이 lock 생성 성공.
        /// lock: userA 있다. -> backend-2는 lock 획득 실패.
        /// TTL(유효시간)도 같이 준다: 락을 잡은 서버가 죽어도 10초 뒤에는 자동 해제
        Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("분산 락 획득 성공: {} (값: {})", lockKey, lockValue);
        } else {
            log.debug("분산 락 획득 실패: {}", lockKey);
            throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
        }
    }

    /// 작업 끝나면 락 삭제
    /**
     [현재 구현은 단순 삭제라서 엄밀한 분산 락은 아니다]
     (1) backend-1 lock 획득.
     (2) 작업이 10초 이상 걸린다.
     (3) lock TTL 만료
     (4) backend-2 lock 획득
     (5) backend-1 작업 종료 후 delete
     (6) ***backend-2 lock을 지워버릴 수 있음.
     **/
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
