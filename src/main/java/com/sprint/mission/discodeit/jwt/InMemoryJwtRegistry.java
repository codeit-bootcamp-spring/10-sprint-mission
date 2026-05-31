package com.sprint.mission.discodeit.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    // <userId, Queue<JwtInformation>>
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    public InMemoryJwtRegistry(
        @Value("${discodeit.jwt.max-active-count:1}") int maxActiveJwtCount,
        JwtTokenProvider jwtTokenProvider) {
        this.maxActiveJwtCount = maxActiveJwtCount;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();
        Queue<JwtInformation> queue = origin.computeIfAbsent(
            userId, k -> new ConcurrentLinkedQueue<>());
        while (queue.size() >= maxActiveJwtCount) {
            JwtInformation removed = queue.poll();
            if (removed != null) {
                log.info("최대 동시 로그인 수 초과로 기존 토큰 무효화: userId={}",
                    removed.userDto().id());
            }
        }
        queue.offer(jwtInformation);
        log.debug("JWT 등록: userId={}", userId);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> removed = origin.remove(userId);
        if (removed != null && !removed.isEmpty()) {
            log.info("사용자의 모든 JWT 무효화: userId={}, count={}", userId, removed.size());
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(info -> accessToken.equals(info.accessToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(info -> refreshToken.equals(info.refreshToken()));
    }

    @Override
    public void rotateJwtInformation(String accessToken, String refreshToken) {
        UUID userId = jwtTokenProvider.getUserId(accessToken);
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue == null || queue.isEmpty()) {
            log.warn("rotate 대상 JwtInformation 없음: userId={}", userId);
            return;
        }
        UserDto userDto = queue.peek().userDto();
        queue.clear();
        queue.offer(new JwtInformation(userDto, accessToken, refreshToken));
        log.debug("JWT 로테이션 완료: userId={}", userId);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        Instant now = Instant.now();
        origin.values().forEach(queue -> queue.removeIf(
            info -> jwtTokenProvider.getExpiration(info.refreshToken()).isBefore(now)));
        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.debug("만료된 JWT 정리 완료: activeUsers={}", origin.size());
    }
}
