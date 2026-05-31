package com.sprint.mission.discodeit.registry;

import com.sprint.mission.discodeit.entity.JwtInformation;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {

  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return false;
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return false;
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return false;
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {

  }

  @Override
  public void clearExpiredJwtInformation() {

  }
}
