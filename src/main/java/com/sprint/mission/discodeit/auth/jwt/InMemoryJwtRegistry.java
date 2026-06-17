package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.auth.dto.JwtInformation;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenCollections = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenCollections = ConcurrentHashMap.newKeySet();

  private final JwtTokenProvider jwtTokenProvider;
  private final int maxActiveJwtCount;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    // 로그인 성공 시 JwtInformation을 등록합니다.
    // 최대 동시 로그인 수(1)를 제어합니다.
    origin.compute(jwtInformation.getUserDto().id(), (k, v) -> {
      if (v == null) {
        v = new ConcurrentLinkedQueue<>();
      }

      if (v.size() >= maxActiveJwtCount) {
        JwtInformation oldest = v.poll();
        if (oldest != null) {
          removeTokens(oldest.getAccessToken(), oldest.getRefreshToken());
        }
      }
      v.add(jwtInformation);
      registerTokens(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

      return v;
    });

  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    // UserId로 해당 유저의 모든 JwtInformation 정보를 삭제합니다.
    origin.computeIfPresent(userId, (k, v) -> {
      v.forEach(jwtInfo -> removeTokens(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken()));
      return null;
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    // JwtInformation이 Registry에 존재하는지 확인합니다.
    // 사용자의 로그인 상태를 판단할 때 활용합니다.
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    // JwtInformation이 Registry에 존재하는지 확인합니다.
    // 필터에서 유효한 토큰인지 확인할 때 활용합니다.
    return accessTokenCollections.contains(accessToken);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    // JwtInformation이 Registry에 존재하는지 확인합니다.
    // 토큰 재발급 시 유효한 토큰인지 확인할 때 활용합니다.
    return refreshTokenCollections.contains(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    // 토큰 재발급 시 토큰 로테이션을 수행합니다.
    origin.computeIfPresent(newJwtInformation.getUserDto().id(), (k, v) -> {

      JwtInformation targetInfo = v.stream()
          .filter(jwtInfo -> jwtInfo.getRefreshToken().equals(refreshToken))
          .findFirst()
          .orElseThrow(InvalidTokenException::new);

      removeTokens(targetInfo.getAccessToken(), targetInfo.getRefreshToken());
      String newAccessToken = newJwtInformation.getAccessToken();
      String newRefreshToken = newJwtInformation.getRefreshToken();

      targetInfo.rotate(newAccessToken, newRefreshToken);
      registerTokens(newAccessToken, newRefreshToken);
      return v;
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)  // 5분마다
  @Override
  public void clearExpiredJwtInformation() {
    // 만료된 JwtInformation을 삭제합니다.
    origin.entrySet().removeIf(entry -> {
      Queue<JwtInformation> jwtInfos = entry.getValue();

      jwtInfos.removeIf(jwtInfo -> {
        if (!jwtTokenProvider.validateToken(jwtInfo.getRefreshToken())) {
          removeTokens(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
          return true;
        }
        return false;
      });

      return jwtInfos.isEmpty();
    });
  }

  private void registerTokens(String accessToken, String refreshToken) {
    accessTokenCollections.add(accessToken);
    refreshTokenCollections.add(refreshToken);
  }

  private void removeTokens(String accessToken, String refreshToken) {
    accessTokenCollections.remove(accessToken);
    refreshTokenCollections.remove(refreshToken);
  }
}
