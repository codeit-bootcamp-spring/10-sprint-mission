package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      value = "userNotifications",
      key = "#receiverId"
  )
  public List<NotificationDto> findAllNotification(UUID receiverId) {
    List<Notification> notifications = notificationRepository.findByReceiverId(receiverId)
        .orElse(List.of());

    return notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  @PreAuthorize("@basicNotificationService.isOwner(#notificationId, #authId)")
  @CacheEvict(value = "userNotifications", key = "#authId")
  public void deleteNotification(UUID notificationId, UUID authId) {
    if (!notificationRepository.existsById(notificationId)) {
      throw new NotificationNotFoundException();
    }
    notificationRepository.deleteById(notificationId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createMessageNotifications(UUID channelId, UUID senderId, String content) {
    Cache cache = cacheManager.getCache("userNotifications");

    List<ReadStatus> list = readStatusRepository.findAllByChannelIdAndUserIdNotAndNotificationEnabled(channelId, senderId, true);

    User sender = userRepository.findById(senderId)
            .orElseThrow(UserNotFoundException::new);

    list.forEach(
        readStatus -> {
          notificationRepository.save(new Notification(
                  sender.getUsername() + " (#" + (
                      readStatus.getChannel().getType().equals(ChannelType.PUBLIC) ? readStatus.getChannel().getName() : "개인 메시지") + ")",
                  content,
                  readStatus.getUser().getId()
              )
          );

          if (cache != null) {
            cache.evict(readStatus.getUser().getId());
          }
        }

    );
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @CacheEvict(
      value = "userNotifications",
      key = "#userId"
  )
  public void createRoleNotification(UUID userId, Role beforeRole, Role afterRole) {
    notificationRepository.save(
        new Notification(
            "권한이 변경되었습니다.",
            beforeRole + " -> " + afterRole,
            userId
        )
    );
  }

  public boolean isOwner(UUID notificationId, UUID receiverId) {
    Notification n = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
    return n.getReceiverId().equals(receiverId);
  }

  @Override
  public void s3UploadFailedNotification(String requestId, UUID binaryContentId, String error){
    userRepository.findAllByRole(Role.ADMIN).forEach(user -> {
      notificationRepository.save(
          new Notification(
              "S3 파일 업로드 실패",
              """
                  RequestId: %s
                  BinaryContentId: %s
                  Error: %s
                  """.formatted(requestId, binaryContentId, error),
              user.getId()
          )
      );

      Cache cache = cacheManager.getCache("userNotifications");
      if (cache != null) {
        cache.evict(user.getId());
      }
    });
  }
}
