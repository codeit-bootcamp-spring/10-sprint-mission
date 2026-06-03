package com.sprint.mission.discodeit.security.jwt.registry;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
    InMemoryJwtRegistry
    -------------------
    메모리 기반의 JWT 상태 저장소
 */
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();     // 사용자 별 JWT 데이터 객체
    private final int maxActiveJwtCount = 1;                                               // 사용자 별 가능한 동시 로그인 수
    private final JwtTokenProvider jwtTokenProvider;

    // 새로운 로그인 정보 등록
    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        // 사용자 별로 JWT 데이터 객체를 저장할 큐(Queue) 생성: 선입선출 구조
        origin.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);

        // 동시 로그인 제한: 새로운 로그인 발생 시, 사용자의 이전 JWT 데이터 객체 삭제
        while (queue.size() >= maxActiveJwtCount) {
            queue.poll();
        }

        // 새로운 JWT 데이터 객체 저장
        queue.offer(jwtInformation);
    }

    // 특정 사용자 강제 로그아웃
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        // 특정 사용자의 데이터 정보 삭제
        origin.remove(userId);
    }

    // 로그인 여부 확인 (사용자 ID)
    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        // 특정 사용자의 큐 조회
        Queue<JwtInformation> queue = origin.get(userId);

        return queue != null && !queue.isEmpty();
    }

    // 로그인 정보 유효성 여부 확인 (AccessToken)
    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                // 일치하는 액세스 토큰이 있다면 로그인 정보 유효
                .anyMatch(information -> information.getAccessToken().equals(accessToken));
    }

    // 로그인 정보 유효성 여부 확인 (RefreshToken)
    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Queue::stream)
                // 일치하는 리프레시 토큰이 있다면 로그인 정보 유효
                .anyMatch(information -> information.getRefreshToken().equals(refreshToken));
    }

    // 토큰 로테이션
    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();
        Queue<JwtInformation> queue = origin.get(userId);

        if (queue != null) {
            // 사용자의 이전 JWT 데이터 정보 삭제 및 새로운 JWT 데이터 정보 추가
            queue.removeIf(information -> information.getRefreshToken().equals(refreshToken));
            queue.offer(newJwtInformation);
        }
    }

    // 만료된 토큰 정리: 5분마다 만료된 토큰 정리
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.values().forEach(queue -> {
            // 토큰이 만료된 사용자의 JWT 데이터 정보 삭제
            queue.removeIf(information -> !jwtTokenProvider.validateToken(information.getRefreshToken()));
        });
        // 모든 토큰이 만료된 사용자의 데이터 정보 삭제
        origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
