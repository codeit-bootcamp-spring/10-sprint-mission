package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry{

    // <userId, Queue<JwtInformation>>
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        origin.computeIfAbsent(userId, k -> new LinkedList<>());

        Queue<JwtInformation> queue = origin.get(userId);

        // 최대 동시 로그인 수 제어 (초과 시 기존 토큰 무효화)
        while (queue.size() >= maxActiveJwtCount) {
            queue.poll();
            log.debug("동시 로그인 제한으로 기존 토큰 무효화: userId={}", userId);
        }

        // 로그인 성공 시 JwtInformation 등록
        queue.add(jwtInformation);
        log.debug("JWT 등록 완료: userId={}", userId);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        // userId로 해당 유저의 모든 JwtInformation 삭제
        origin.remove(userId);
        log.debug("JWT 무효화 완료: userId={}", userId);
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
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.values().stream()
                .flatMap(Collection::stream)
                .filter(info -> info.getRefreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(info -> info.rotate(
                        newJwtInformation.getAccessToken(),
                        newJwtInformation.getRefreshToken()
                ));
        log.debug("JWT 로테이션 완료");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.forEach((userId, queue) ->
                queue.removeIf(info -> !jwtTokenProvider.validate(info.getRefreshToken()))
        );
        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.debug("만료된 JWT 정보 삭제 완료");
    }
}
