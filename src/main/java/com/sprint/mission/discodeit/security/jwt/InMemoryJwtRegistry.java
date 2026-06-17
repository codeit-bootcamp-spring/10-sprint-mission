package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    Queue<JwtInformation> queue = origin.computeIfAbsent(
        jwtInformation.userId(),
        key -> new ConcurrentLinkedQueue<>()
    );

    synchronized (queue) {
      while (queue.size() >= maxActiveJwtCount) {
        queue.poll();
      }

      queue.offer(jwtInformation);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.values().forEach(queue -> {
      synchronized (queue) {
        queue.removeIf(jwtInformation ->
            jwtInformation.refreshToken().equals(refreshToken)
        );
      }
    });

    removeEmptyQueues();
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue == null || queue.isEmpty()) {
      return false;
    }

    Instant now = Instant.now();

    return queue.stream()
        .anyMatch(jwtInformation ->
            jwtInformation.refreshTokenExpiresAt().isAfter(now)
        );
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    Instant now = Instant.now();

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwtInformation ->
            jwtInformation.accessToken().equals(accessToken)
                && jwtInformation.accessTokenExpiresAt().isAfter(now)
        );
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    Instant now = Instant.now();

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwtInformation ->
            jwtInformation.refreshToken().equals(refreshToken)
                && jwtInformation.refreshTokenExpiresAt().isAfter(now)
        );
  }

  @Override
  public void rotateJwtInformation(
      String oldRefreshToken,
      JwtInformation newJwtInformation
  ) {
    invalidateJwtInformationByRefreshToken(oldRefreshToken);
    registerJwtInformation(newJwtInformation);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Instant now = Instant.now();

    origin.values().forEach(queue -> {
      synchronized (queue) {
        queue.removeIf(jwtInformation ->
            jwtInformation.refreshTokenExpiresAt().isBefore(now)
        );
      }
    });

    removeEmptyQueues();
  }

  private void removeEmptyQueues() {
    Iterator<Map.Entry<UUID, Queue<JwtInformation>>> iterator =
        origin.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<UUID, Queue<JwtInformation>> entry = iterator.next();

      if (entry.getValue().isEmpty()) {
        iterator.remove();
      }
    }
  }
}