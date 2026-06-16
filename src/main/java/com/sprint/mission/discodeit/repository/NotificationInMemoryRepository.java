package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.NotificationDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationInMemoryRepository {

  // 유저ID -> (알림ID -> 알림DTO)
  private final Map<UUID, Map<UUID, NotificationDto>> storage = new ConcurrentHashMap<>();

  // 알림 저장
  public void save(NotificationDto notification) {
    storage.computeIfAbsent(notification.receiverId(), k -> new ConcurrentHashMap<>())
        .put(notification.id(), notification);
  }

  // 유저의 모든 알림 조회
  public List<NotificationDto> findAllByUserId(UUID userId) {
    Map<UUID, NotificationDto> userNotifications = storage.getOrDefault(userId, Collections.emptyMap());
    return new ArrayList<>(userNotifications.values());
  }

  // 특정 알림 단건 조회
  public Optional<NotificationDto> findById(UUID userId, UUID notificationId) {
    Map<UUID, NotificationDto> userNotifications = storage.get(userId);
    if (userNotifications == null) return Optional.empty();
    return Optional.ofNullable(userNotifications.get(notificationId));
  }

  // 알림 삭제
  public void delete(UUID userId, UUID notificationId) {
    Map<UUID, NotificationDto> userNotifications = storage.get(userId);
    if (userNotifications != null) {
      userNotifications.remove(notificationId);
    }
  }

  // 전체 저장소에서 특정 알림 ID가 존재하는지 확인
  public boolean existsById(UUID notificationId) {
    return storage.values().stream()
        .anyMatch(userNotifications -> userNotifications.containsKey(notificationId));
  }
}