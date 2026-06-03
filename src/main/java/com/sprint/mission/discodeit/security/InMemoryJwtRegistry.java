package com.sprint.mission.discodeit.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    public InMemoryJwtRegistry(
            @Value("${jwt.max-active-count:1}") int maxActiveJwtCount,
            JwtTokenProvider jwtTokenProvider) {
        this.maxActiveJwtCount = maxActiveJwtCount;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        Queue<JwtInformation> queue = origin.computeIfAbsent(userId, k -> new LinkedList<>());

        while (queue.size() >= maxActiveJwtCount) {
            JwtInformation evicted = queue.poll();
            if (evicted != null) {
                log.debug("최대 동시 로그인 초과로 기존 세션 무효화 - userId: {}", userId);
            }
        }

        queue.add(jwtInformation);
        log.debug("JwtInformation 등록 - userId: {}", userId);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
        log.debug("JwtInformation 전체 무효화 - userId: {}", userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue != null) {
            queue.removeIf(info -> info.getRefreshToken().equals(oldRefreshToken));
            queue.add(newJwtInformation);
            log.debug("JwtInformation 토큰 Rotation 완료 - userId: {}", userId);
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.forEach(
                (userId, queue) -> queue.removeIf(info -> !jwtTokenProvider.validateToken(info.getRefreshToken())));
        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.debug("만료된 JwtInformation 정리 완료");
    }
}
