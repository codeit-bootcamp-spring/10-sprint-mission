package com.sprint.mission.discodeit.security.jwt;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // maxActiveCount 초과 시 가장 오래된 것부터 제거하기 편한 Queue를 사용 (FIFO 구조 활용)
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final JwtTokenProvider jwtTokenProvider;

  // 한 유저당 허용할 최대 동시 활성 토큰 수 (기기 수 제한)
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(JwtTokenProvider jwtTokenProvider,
      @Value("${discodeit.jwt.max-active-count}") int maxActiveJwtCount) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  // 새 JWT 정보를 등록. 활성 토큰 수가 maxActiveJwtCount 도달하면 가장 오래된 것부터 제거 후 추가
  @Override
  public void registerJwtInformation(JwtInformation info) {
    // computeIfAbsent: userId 키가 없으면 새 Queue를 만들어서 넣고 반환 / 있으면 기존 Queue를 그냥 반환
    // 이 userId의 Queue를 가져오되, 없으면 새로 만들기
    Queue<JwtInformation> queue = origin.computeIfAbsent(
        info.getUserDto().id(), id -> new ConcurrentLinkedQueue<>()
    );
    // FIXME: size-check -> poll -> offer은 원자적이지 않음 (멀티스레드 환경에서 동시 진입 시 문제 발생 가능)
    // 두 스레드가 동시에 진입하면 둘 다 size check를 통과하고 둘 다 offer해서 maxActiveJwtCount + 1개가 들어갈 수 있다.
    while (queue.size() >= maxActiveJwtCount) {
      queue.poll(); // 가장 먼저 들어온 것 꺼냄
    }
    queue.offer(info); // 새 JwtInformation(방금 로그인한 기기)을 Queue 뒤에 추가한다.
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId); // userId 키 자체를 삭제 (로그아웃(전체), 계정 정지, 비밀번호 변경 등에 활용)
  }

  // userId로 조회
  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId); // userId로 로그인한 기록 없으면 null 반환
    return queue != null && !queue.isEmpty(); // queue.isEmpty()가 false이면 현재 활성 JwtInformation 있다는 것
  }

  // accessToken으로 조회
  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream() // queue 컬렉션을 stream으로 뽑고
        .flatMap(Queue::stream) // 각 queue를 풀어서 하나의 stream으로
        // stream 순회 하면서 accessToken과 일치하는 JwtInformation이 하나라도 있으면 true 반환
        .anyMatch(info -> accessToken.equals(info.getAccessToken()));
  }

  // refreshToken으로 조회
  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> refreshToken.equals(info.getRefreshToken()));
  }

  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo) {
    // 해당 유저의 Queue 꺼내서
    Queue<JwtInformation> queue = origin.get(newInfo.getUserDto().id());
    if (queue == null) {
      return; // 이미 로그아웃된 상태
    }
    // oldRefreshToken과 일치하는 JwtInformation 찾기
    queue.stream()
        .filter(info -> oldRefreshToken.equals(info.getRefreshToken()))
        .findFirst()
        // 찾은 객체의 토큰을 새 토큰으로 교체
        .ifPresent(info -> info.rotate(newInfo.getAccessToken(), newInfo.getRefreshToken()));
  }

  // JWT는 만료되어도 registry에 그대로 남는다 -> 5분마다 자동 실행되어 만료된 JwtInformation을 정리
  @Override
  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void clearExpiredJwtInformation() {
    // 모든 Map.entry 순회 하면서
    for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
      // 만료된 JwtInformation 제거
      Queue<JwtInformation> queue = entry.getValue();
      queue.removeIf(info -> !jwtTokenProvider.validateToken(info.getRefreshToken()));
      // Queue가 비면 Map.entry 자체를 삭제
      if (queue.isEmpty()) {
        origin.remove(entry.getKey(), queue);
      }
    }
  }
}
