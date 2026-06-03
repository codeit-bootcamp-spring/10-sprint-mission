package com.sprint.mission.discodeit.jwt;

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

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;

  public InMemoryJwtRegistry(@Value("${jwt.max-active-jwt-count:1}") int maxActiveJwtCount,
      JwtTokenProvider jwtTokenProvider) {
    this.maxActiveJwtCount = maxActiveJwtCount;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userDto().id();

    Queue<JwtInformation> queue = origin.computeIfAbsent(userId,
        k -> new ConcurrentLinkedQueue<>());

    while (queue.size() >= maxActiveJwtCount) {
      queue.poll();
    }

    queue.add(jwtInformation);

  }

  @Override
  public void invalidateJwtInformation(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId) && !origin.get(userId).isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .anyMatch(queue -> queue.stream()
            .anyMatch(info -> info.accessToken().equals(accessToken)));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .anyMatch(queue -> queue.stream()
            .anyMatch(info -> info.refreshToken().equals(refreshToken)));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    UUID userId = newJwtInformation.userDto().id();
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue != null) {
      queue.removeIf(info -> info.refreshToken().equals(refreshToken));
      queue.add(newJwtInformation);
    }

  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.values().forEach(queue ->
        queue.removeIf(info -> {
          try {
            jwtTokenProvider.getClaims(info.refreshToken());
            return false;
          } catch (Exception e) {
            return true;
          }
        })
    );
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());

  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.entrySet().stream()
        .filter(entry -> entry.getValue().stream()
            .anyMatch(info -> info.refreshToken().equals(refreshToken)))
        .map(Map.Entry::getKey)
        .findFirst()
        .ifPresent(userId -> origin.remove(userId));
  }
}
