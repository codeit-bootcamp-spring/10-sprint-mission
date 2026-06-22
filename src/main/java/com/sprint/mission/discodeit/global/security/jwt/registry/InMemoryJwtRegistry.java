package com.sprint.mission.discodeit.global.security.jwt.registry;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationErrorCode;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.jwt.registry.store-type", havingValue = "in-memory")
public class InMemoryJwtRegistry implements JwtRegistry {

  @Value("${app.jwt.registry.session.max-retained}")
  private int sessionMaxRetained;
  @Value("${app.jwt.registry.session.max-concurrent}")
  private int sessionMaxConcurrent;

  private final Map<UUID, Queue<RefreshToken>> retainedDevice = new ConcurrentHashMap<>();
  private final Map<UUID, Queue<RefreshToken>> activeSessions = new ConcurrentHashMap<>();

  @Override
  public void register(RefreshToken refreshToken) {
    register(refreshToken, retainedDevice, sessionMaxRetained);
    register(refreshToken, activeSessions, sessionMaxConcurrent);
  }

  @Override
  public RefreshToken findByToken(String refreshToken) {
    return retainedDevice.values().stream().flatMap(Collection::stream)
        .filter(token -> token.hasToken(refreshToken))
        .findFirst()
        .orElseThrow(
            () -> new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND));
  }

  @Override
  public void invalidateAllByUserId(UUID userId) {
    retainedDevice.remove(userId);
    activeSessions.remove(userId);
  }

  @Override
  public void invalidateByToken(String refreshToken) {
    invalidate(refreshToken, retainedDevice);
    invalidate(refreshToken, activeSessions);
  }

  @Override
  public void clearExpiredTokens() {
    Instant now = Instant.now();
    clearExpiredTokens(retainedDevice, now);
    clearExpiredTokens(activeSessions, now);
  }

  @Override
  public boolean isActiveSession(UUID userId, String device) {
    Queue<RefreshToken> queue = activeSessions.get(userId);
    if (queue == null || queue.isEmpty()) {
      return false;
    }
    return queue.stream().anyMatch(token -> token.getDevice().equals(device));
  }

  private void clearExpiredTokens(Map<UUID, Queue<RefreshToken>> storage, Instant time) {
    storage.values().forEach(queue ->
        queue.removeIf(token -> token.isExpired(time)));
  }

  private void register(RefreshToken token, Map<UUID, Queue<RefreshToken>> storage, int size) {
    Queue<RefreshToken> queue = storage.computeIfAbsent(token.getUser().getId(),
        k -> new ConcurrentLinkedQueue<>());
    queue.removeIf(existing -> existing.getDevice().equals(token.getDevice()));
    queue.offer(token);
    while (queue.size() > size) {
      queue.poll();
    }
  }

  private void invalidate(String refreshToken, Map<UUID, Queue<RefreshToken>> storage) {
    for (Queue<RefreshToken> queue : storage.values()) {
      if (queue.removeIf(token -> token.getToken().equals(refreshToken))) {
        return;
      }
    }
  }
}
