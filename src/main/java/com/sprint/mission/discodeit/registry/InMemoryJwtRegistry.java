package com.sprint.mission.discodeit.registry;

import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.JwtInformation;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // Jwt를 저장할 Map 자료구조 origin
  // <userId, Queue<JwtInformation>> 형태
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

  // 최대 동시 로그인 수
  private final int maxActiveJwtCount = 1;

  // 로그인 성공시 JwtInformation을 등록하고 최대 동시 로그인 수를 제어
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    Objects.requireNonNull(jwtInformation, "Jwt 정보가 유효하지 않습니다.");

    // origin의 식별자를 요청으로 들어온 jwtInformation으로 부터 읽음.
    UUID userId = jwtInformation.getUserDto().id();

    // origin 내에 userId가 없으면 새로운 큐를 생성하고 있으면 무시
    // origin 내에 userId가 있으면 origin.get(userId)가 jwtQueue가 됨.
    Queue<JwtInformation> jwtQueue =
        origin.computeIfAbsent(userId, key -> new ConcurrentLinkedQueue<>());

    // 요청으로 들어온 Jwt 정보를 큐에 삽입
    jwtQueue.add(jwtInformation);

    // 삽입 후의 jwtQueue 사이즈가 최대 동시 로그인 수보다 크면 poll()
    while (jwtQueue.size() > maxActiveJwtCount) {
      jwtQueue.poll();
    }
  }

  // userId로 해당 유저의 모든 JwtInformation 정보 삭제
  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    if (!origin.containsKey(userId)) {
      throw new NoSuchElementException("해당 userId를 찾을 수 없습니다.");
    }

    // userId에 매칭되는 jwtQueue 가져오고
    Queue<JwtInformation> jwtQueue = origin.get(userId);

    // 해당 큐가 empty일 때 까지 poll()
    while (jwtQueue.isEmpty()) {
      jwtQueue.poll();
    }

  }

  // userId에 매칭되는 JwtInformation이 Registry에 존재하는지 확인
  // 사용자의 로그인 상태를 판단할 때 활용된다.
  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId) && !origin.get(userId).isEmpty();
  }

  // accessToken에 매칭되는 JwtInformation이 Registry에 존재하는지 확인
  // 필터에서 유효한 토큰인지 확인할 때 활용된다.
  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    Objects.requireNonNull(accessToken, "AccessToken이 유효하지 않습니다.");

    return origin.values()
        .stream()
        .flatMap(Collection::stream)
        .anyMatch(jwtInformation -> accessToken.equals(jwtInformation.getAccessToken()));
  }

  // RefreshToken에 매칭되는 JwtInformation이 Registry에 존재하는지 확ㅇ니
  // 토큰 재발급 시 토큰 로테이션을 수행한다.
  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    Objects.requireNonNull(refreshToken, "RefreshToken이 유효하지 않습니다.");

    return origin.values()
        .stream()
        .flatMap(Collection::stream)
        .anyMatch(jwtInformation -> refreshToken.equals(jwtInformation.getRefreshToken()));
  }

  // 토큰 재발급 시 토큰 로테이션을 수행
  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    Objects.requireNonNull(refreshToken, "RefreshToken이 유효하지 않습니다!");

    for (Queue<JwtInformation> jwtQueue : origin.values()) {
      JwtInformation oldJwtInformation = jwtQueue.stream()
          .filter(jwtInformation -> refreshToken.equals(jwtInformation.getRefreshToken()))
          .findFirst()
          .orElse(null);

      if (oldJwtInformation != null) {
        jwtQueue.remove(oldJwtInformation);
        jwtQueue.add(newJwtInformation);
        return;
      }
    }

  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.values()
        .forEach(jwtQueue -> jwtQueue.removeIf(jwtInformation ->
            JwtTokenProvider.isExpired(jwtInformation.getRefreshToken())));

  }

  @Override
  public void removeJwtInformationByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId가 유효하지 않습니다!");
    origin.remove(userId);
  }

  // RefreshToken에 매칭되는 JwtInformation을 삭제하는 메서드
  @Override
  public void removeJwtInformationByRefreshToken(String refreshToken) {
    Objects.requireNonNull(refreshToken, "RefreshToken이 유효하지 않습니다!");
    // jwtQueue를 순회하면서 jwtInformation의 RefreshToken이 삭제하고자 하는 RefreshToken 값과 일치하는 지 확인하고 삭제
    origin.values()
        .forEach(jwtQueue ->
            jwtQueue.removeIf(jwtInformation ->
                refreshToken.equals(jwtInformation.getRefreshToken()))
        );

    // 삭제 작업 수행 후, JwtQueue가 비어있는 origin(Map) 요소는 삭제
    origin.entrySet().removeIf(entrySet -> entrySet.getValue().isEmpty());
  }
}
