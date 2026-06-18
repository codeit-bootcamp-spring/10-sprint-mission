//package com.sprint.mission.discodeit.security.jwt;
//
//import com.sprint.mission.discodeit.dto.JwtInformation;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class InMemoryJwtRegistry implements JwtRegistry {
//
//  private final Map<UUID, JwtInformation> storage = new ConcurrentHashMap<>();
//  private final Map<String, JwtInformation> accessIndex = new ConcurrentHashMap<>();
//  private final Map<String, JwtInformation> refreshIndex = new ConcurrentHashMap<>();
//
//  @Override
//  public void registerJwtInformation(JwtInformation info) {
//    JwtInformation old = storage.remove(info.getUserId());
//
//    if (old != null) {
//      accessIndex.remove(old.getAccessToken());
//      refreshIndex.remove(old.getRefreshToken());
//    }
//
//    storage.put(info.getUserId(), info);
//    accessIndex.put(info.getAccessToken(), info);
//    refreshIndex.put(info.getRefreshToken(), info);
//  }
//
//  @Override
//  public void invalidateJwtInformationByUserId(UUID userId) {
//    JwtInformation info = storage.remove(userId);
//
//    if (info != null) {
//      accessIndex.remove(info.getAccessToken());
//      refreshIndex.remove(info.getRefreshToken());
//    }
//  }
//
//  @Override
//  public boolean hasActiveJwtInformationByUserId(UUID userId) {
//    return storage.containsKey(userId);
//  }
//
//  @Override
//  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
//    return accessIndex.containsKey(accessToken);
//  }
//
//  @Override
//  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
//    return refreshIndex.containsKey(refreshToken);
//  }
//
//  @Override
//  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo) {
//    JwtInformation oldInfo = refreshIndex.get(oldRefreshToken);
//
//    if (oldInfo == null) {
//      return;
//    }
//
//    UUID userId = oldInfo.getUserId();
//
//    JwtInformation removed = storage.remove(userId);
//
//    if (removed != null) {
//      accessIndex.remove(removed.getAccessToken());
//      refreshIndex.remove(removed.getRefreshToken());
//    }
//
//    registerJwtInformation(newInfo);
//  }
//
//  @Override
//  @Scheduled(fixedDelay = 1000 * 60 * 5)
//  public void clearExpiredJwtInformation() {
//    accessIndex.values().stream()
//        .filter(JwtInformation::isExpired)
//        .distinct()
//        .forEach(this::removeExpired);
//  }
//
//  // 헬퍼 메서드
//  private void removeExpired(JwtInformation info) {
//    UUID userId = info.getUserId();
//
//    storage.remove(userId);
//
//    accessIndex.remove(info.getAccessToken());
//    refreshIndex.remove(info.getRefreshToken());
//  }
//}
