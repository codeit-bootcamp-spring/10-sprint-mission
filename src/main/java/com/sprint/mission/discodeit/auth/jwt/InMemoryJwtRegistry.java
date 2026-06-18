package com.sprint.mission.discodeit.auth.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry{
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1; //  최대 동시 로그인 수 1로 제어
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().getId();

        // 큐가 없으면 스레드 안전한 큐로 새로 생성.
        origin.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);

        // 새 토큰 정보를 큐에 추가.
        queue.add(jwtInformation);

        // 동일한 계정으로 로그인 시 기존 로그인 세션 무효화
        while (queue.size() > maxActiveJwtCount) {
            queue.poll(); // 가장 오래된 토큰을 무효화
        }
        log.info("JWT 정보 등록 완료. 현재 활성 기기 수: {}", queue.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
        log.info("해당 유저의 모든 토큰 무효화 완료. userId = {}", userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        // 사용자의 로그인 여부를 판단
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(info -> info.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().getId();

        origin.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);

        boolean isRotated = false;

        for (JwtInformation info : queue) {
            if (info.getRefreshToken().equals(oldRefreshToken)) {
                info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                isRotated = true;
                break;
            }
        }

        if (!isRotated) {
            queue.add(newJwtInformation);

            // 최대 동시 로그인 수 제어 유지
            while (queue.size() > maxActiveJwtCount) {
                queue.poll();
            }
        }

        log.info("[JwtRegistry] 토큰 로테이션 완료. userId: {}", userId);
    }

    // 5분마다 주기적으로 만료된 토큰 청소
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        log.info("[스케줄러] 만료된 JWT 메모리 청소 시작");

        origin.values().forEach(queue -> {
            queue.removeIf(info -> {
                boolean isExpired = !jwtTokenProvider.validateToken(info.getRefreshToken());
                if (isExpired) {
                    log.info("만료된 토큰 삭제됨. User: {}", info.getUserDto().getUsername());
                }
                return isExpired;
            });
        });

        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
