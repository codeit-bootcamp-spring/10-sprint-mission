package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // 유저별(ID 기준)로 토큰 상태를 관리하는 저장소
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>(); // 멀티스레드 환경에서 안전한 Map과 Queue 사용
  private final int maxActiveJwtCount = 1; // 동시 로그인 1개 제한

  // 로그인 성공 시 토큰 정보 등록
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    // JwtInformation 안의 userDto에서 ID를 추출하여 유저별 토큰 큐에 저장
    UUID userId = jwtInformation.getUserDto().id();

    origin.compute(userId, (key, queue) -> { // compute -> 특정 키에 대해 원자적 연산 보장
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      queue.add(jwtInformation);

      // 최대 동시 로그인 수 초과 시 가장 오래된 토큰 폐기
      while (queue.size() > maxActiveJwtCount) {
        queue.poll();
        log.info("Previous session expired due to concurrent login limit - userId: {}", userId);
      }
      return queue;
    });
  }

  // 특정 유저의 모든 토큰 정보를 무효화
  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
    log.info("All tokens invalidated for user (forced logout) - userId: {}", userId);
  }

  // 상태 확인 (유저 ID 기준 - 현재 로그인 상태인지 파악)
  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
  }

  // 상태 확인 (Access Token 기준 - 필터에서 유효성 검사 시)
  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getAccessToken().equals(accessToken));
  }

  // 상태 확인 (Refresh Token 기준 - 재발급 시 유효성 검사)
  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
  }

  // 토큰 로테이션 (Refresh Token 재발급 시 기존 토큰 정보 업데이트)
  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    UUID userId = newJwtInformation.getUserDto().id();

    origin.computeIfPresent(userId, (key, queue) -> {
      // UML에 JwtInformation.rotate()가 존재하지만,
      // 멀티스레드 환경의 원자성(Atomicity) 보장과 파라미터(newJwtInformation) 활용을 위해
      // 기존 객체 내부를 조작하지 않고 낡은 객체를 지운 뒤 새 객체를 큐에 넣는 방식을 채택함.
      queue.removeIf(info -> info.getRefreshToken().equals(refreshToken));
      queue.add(newJwtInformation);
      return queue;
    });
  }

  // 만료된 토큰 청소 (스케줄러용)
  @Scheduled(fixedDelay = 1000 * 60 * 5) // 5분마다 실행
  @Override
  public void clearExpiredJwtInformation() {
    Instant now = Instant.now();
    int removedCount = 0;

    for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      Queue<JwtInformation> queue = entry.getValue();

      // JwtInformation의 expiresAt 필드를 활용하여 만료된 토큰 검사
      boolean removed = queue.removeIf(info -> info.getExpiresAt().isBefore(now));
      if (removed) {
        removedCount++;
      }

      // 큐가 비면 메모리 누수 방지를 위해 맵에서 유저 삭제
      if (queue.isEmpty()) {
        origin.remove(entry.getKey());
      }
    }

    if (removedCount > 0) {
      log.info("Expired token information cleared - affected users: {}", removedCount);
    }
  }
}
