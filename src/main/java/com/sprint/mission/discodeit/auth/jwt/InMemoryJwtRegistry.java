package com.sprint.mission.discodeit.auth.jwt;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  // 단일 세션 정책: 사용자별 하나의 활성 토큰 정보만 유지
  private final Map<UUID, JwtInformation> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation info) {
    origin.compute(info.getUserDto().id(), (key, oldInfo) -> {
      if (oldInfo != null) {
        removeTokenIndex(oldInfo.getAccessToken(), oldInfo.getRefreshToken());
      }
      addTokenIndex(info.getAccessToken(), info.getRefreshToken());
      return info;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.computeIfPresent(userId, (key, oldInfo) -> {
      removeTokenIndex(oldInfo.getAccessToken(), oldInfo.getRefreshToken());
      return null;
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return accessTokenIndexes.contains(accessToken);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return refreshTokenIndexes.contains(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newInfo) {
    origin.computeIfPresent(newInfo.getUserDto().id(), (key, oldInfo) -> {
      if (oldInfo.getRefreshToken().equals(refreshToken)) {
        removeTokenIndex(oldInfo.getAccessToken(), oldInfo.getRefreshToken());
        addTokenIndex(newInfo.getAccessToken(), newInfo.getRefreshToken());
        return newInfo;
      }
      return oldInfo;
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.entrySet().removeIf(entry -> {
      JwtInformation info = entry.getValue();
      boolean isExpired = !jwtTokenProvider.validateToken(info.getAccessToken()) ||
          !jwtTokenProvider.validateToken(info.getRefreshToken());

      if (isExpired) {
        removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
      }
      return isExpired;
    });
  }

  private void addTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.add(accessToken);
    refreshTokenIndexes.add(refreshToken);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.remove(accessToken);
    refreshTokenIndexes.remove(refreshToken);
  }
}
