package com.sprint.mission.discodeit.registry.inmemory;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.exception.user.DiscodeitUnauthorizedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    // 로그인 성공 시 JwtInformation을 등록합니다.
    // 최대 동시 로그인 수(1)를 제어합니다.
    Queue<JwtInformation> queue = origin.computeIfAbsent(jwtInformation.userDto().id(),
        userId -> new ConcurrentLinkedQueue<>());

    while (queue.size() >= maxActiveJwtCount) {
      queue.poll();
    }

    queue.offer(jwtInformation);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    // UserId로 해당 유저의 모든 JwtInformation 정보를 삭제합니다.
    if (userId == null) {
      throw new UserNotFoundException();
    }
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    // 사용자의 로그인 상태를 판단할 때 활용합니다.
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    // 필터에서 유효한 토큰인지 확인할 때 활용합니다.
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> info
            .accessToken().equals(accessToken)
        );
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    //  토큰 재발급 시 유효한 토큰인지 확인할 때 활용합니다.
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> info
            .refreshToken().equals(refreshToken)
        );
  }

  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
    // 토큰 재발급 시 토큰 로테이션을 수행합니다.
    UUID userId = newJwtInformation.userDto().id();
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      boolean isRemoved = queue.removeIf(info -> info.refreshToken().equals(oldRefreshToken));
      if (!isRemoved) {
        log.warn("토큰이 삭제되지 않음 RefreshToken={}", oldRefreshToken);
        throw new DiscodeitUnauthorizedException();
      }
      queue.offer(newJwtInformation);
    } else {
      throw new DiscodeitUnauthorizedException();
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    //만료된 JwtInformation을 삭제합니다.
    log.debug("만료된 JwtInformation 삭제 스케줄러 가동");
    origin.values().forEach(queue -> queue
        .removeIf(info -> jwtTokenProvider.isExpired(info.refreshToken()))
    );
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
