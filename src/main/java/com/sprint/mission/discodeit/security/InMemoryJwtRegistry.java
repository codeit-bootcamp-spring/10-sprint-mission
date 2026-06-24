package com.sprint.mission.discodeit.security;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.jwt.registry", havingValue = "memory", matchIfMissing = true)
public class InMemoryJwtRegistry implements JwtRegistry {

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
        UUID userId = jwtInformation.getUserDto().id();
        origin.compute(userId, (id, queue) -> {
            Queue<JwtInformation> q = (queue == null) ? new ConcurrentLinkedQueue<>() : queue;
            // 최대 동시 로그인 수 초과 시 가장 오래된 것부터 제거
            while (q.size() >= maxActiveJwtCount) {
                q.poll();
            }
            q.offer(jwtInformation);
            return q;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> q = origin.get(userId);
        return q != null && !q.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(info -> accessToken.equals(info.getAccessToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(info -> refreshToken.equals(info.getRefreshToken()));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        for (Queue<JwtInformation> q : origin.values()) {
            for (JwtInformation info : q) {
                if (refreshToken.equals(info.getRefreshToken())) {
                    info.rotate(
                            newJwtInformation.getAccessToken(),
                            newJwtInformation.getRefreshToken()
                    );
                    return;
                }
            }
        }
        log.warn("rotateJwtInformation: 매칭되는 refreshToken을 찾지 못함");
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        // 리프레시 토큰이 만료된 항목만 제거 (액세스 토큰은 정상적인 재발급 대상)
        origin.forEach((userId, q) -> {
            q.removeIf(info -> isTokenInvalid(info.getRefreshToken()));
            if (q.isEmpty()) {
                origin.remove(userId, q);
            }
        });
    }

    private boolean isTokenInvalid(String token) {
        try {
            jwtTokenProvider.getClaims(token);
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }
}
