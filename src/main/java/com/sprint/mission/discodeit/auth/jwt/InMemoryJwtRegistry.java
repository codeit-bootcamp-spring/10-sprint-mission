package com.sprint.mission.discodeit.auth.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class InMemoryJwtRegistry implements JwtRegistry{

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    public InMemoryJwtRegistry(JwtTokenProvider jwtTokenProvider) {
        this.maxActiveJwtCount = 1;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        Queue<JwtInformation> jwtInformationQueue = origin.computeIfAbsent(userId, key -> new ConcurrentLinkedDeque<>());

        jwtInformationQueue.add(jwtInformation);

        while (jwtInformationQueue.size() > maxActiveJwtCount) {
            jwtInformationQueue.poll();
        }
    }

    @Override
    public void invalidateJwtInformationUserId(UUID userId) {
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> jwtInformationQueue = origin.get(userId);

        return jwtInformationQueue != null && !jwtInformationQueue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(jwtInformation -> jwtInformation.getAccessToken().equals(accessToken) && isValid(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken) && isValid(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();
        Queue<JwtInformation> jwtInformationQueue = origin.get(userId);

        if (jwtInformationQueue == null) {
            throw new RuntimeException("No active JWT information found for user ID: " + userId);
        }

        boolean removed = jwtInformationQueue.removeIf(jwtInformation -> jwtInformation.getRefreshToken()
                .equals(refreshToken));

        if (!removed) {
            throw new RuntimeException("No active JWT information found for refresh token: " + refreshToken);
        }

        jwtInformationQueue.add(newJwtInformation);

        while (jwtInformationQueue.size() > maxActiveJwtCount) {
            jwtInformationQueue.poll();
        }

    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> jwtInformationQueue = entry.getValue();
            jwtInformationQueue.removeIf(jwtInformation -> !isValid(jwtInformation.getRefreshToken()));

            return jwtInformationQueue.isEmpty();
        });
    }

    private boolean isValid(String token) {
        try {
            jwtTokenProvider.getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
