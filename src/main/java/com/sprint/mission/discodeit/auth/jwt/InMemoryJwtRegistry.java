package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

  private final int maxActiveJwtCount = 1; // 최대 동시 로그인 수

  private final JwtTokenProvider jwtTokenProvider;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;
  private final ApplicationEventPublisher applicationEventPublisher;


  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    Queue<JwtInformation> queue = origin.computeIfAbsent(jwtInformation.userDto().id(),
        i -> new ConcurrentLinkedQueue<>());
    while (queue.size() >= maxActiveJwtCount) {
      queue.poll();
    }
    queue.add(jwtInformation);
    UserDto userDto = jwtInformation.userDto();
    eventPublisher.publishEvent(new UserUpdatedEvent(
        new UserDto(
            userDto.id(),
            userDto.username(),
            userDto.email(),
            userDto.role(),
            userDto.profile(),
            true,
            userDto.createdAt(),
            userDto.updatedAt()
        )
    ));
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.remove(userId);
    if (queue != null && !queue.isEmpty()) {
      UserDto userDto = queue.poll().userDto();
      eventPublisher.publishEvent(new UserUpdatedEvent(
          new UserDto(
              userDto.id(),
              userDto.username(),
              userDto.email(),
              userDto.role(),
              userDto.profile(),
              false,
              userDto.createdAt(),
              userDto.updatedAt()
          )
      ));
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    // 사용자 로그인 여부
    Queue<JwtInformation> queue = origin.getOrDefault(userId, null);
    if (queue == null || queue.isEmpty()) {
      return false;
    }
    return queue.stream()
        .anyMatch(info -> jwtTokenProvider.validateToken(info.accessToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    // 필터에서 유효 토큰 확인용
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(i -> i.accessToken().equals(accessToken)
            && jwtTokenProvider.validateToken(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    // 토큰 재 발급 시 유효 토큰 확인용
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(i -> i.refreshToken().equals(refreshToken)
            && jwtTokenProvider.validateToken(refreshToken));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    UUID userId = newJwtInformation.userDto().id();
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue != null) {
      queue.stream()
          .filter(i -> i.refreshToken().equals(refreshToken))
          .findFirst()
          .ifPresent(i -> {
            queue.remove(i);
            queue.add(i.rotate(newJwtInformation.accessToken(), newJwtInformation.refreshToken()));
          });
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) -> {
      queue.removeIf(i -> !jwtTokenProvider.validateToken(i.refreshToken()));
    });

    List<UUID> targets = new ArrayList<>();

    origin.forEach((userId, queue) -> {
      if (queue.isEmpty()) {
        targets.add(userId);
      }
    });

    if (!targets.isEmpty()) {
      targets.forEach(this::invalidateJwtInformationByUserId);

      Cache userListCache = cacheManager.getCache("userListCache");
      if (userListCache != null) {
        userListCache.evict("with_session");
      }
    }
  }

  @Override
  public Set<UUID> getActiveUserIds() {
    return origin.keySet();
  }
}
