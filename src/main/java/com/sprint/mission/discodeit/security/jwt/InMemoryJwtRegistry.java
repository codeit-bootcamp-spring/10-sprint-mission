package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry{

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();

        Queue<JwtInformation> queue = new ConcurrentLinkedQueue<>();
        queue.add(jwtInformation);

        origin.put(userId, queue);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);

        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(jwtInformation ->
                        jwtInformation.accessToken().equals(accessToken)
                );
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(jwtInformation ->
                        jwtInformation.refreshToken().equals(refreshToken)
                );
    }

    @Override
    public void rotateJwtInformation(
            String refreshToken,
            JwtInformation newJwtInformation
    ) {
        invalidateJwtInformationByRefreshToken(refreshToken);
        registerJwtInformation(newJwtInformation);
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        origin.values().forEach(queue ->
                queue.removeIf(jwtInformation ->
                        !jwtTokenProvider.validationToken(jwtInformation.accessToken())
                                || !jwtTokenProvider.validationToken(jwtInformation.refreshToken())
                )
        );

        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void invalidateJwtInformationByRefreshToken(String refreshToken) {
        Iterator<Map.Entry<UUID, Queue<JwtInformation>>> iterator =
                origin.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Queue<JwtInformation>> entry = iterator.next();

            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(jwtInformation ->
                    jwtInformation.refreshToken().equals(refreshToken)
            );

            if (queue.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
