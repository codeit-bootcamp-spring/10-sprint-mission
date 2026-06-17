package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    MessageDto messageDto = event.messageDto();
    log.debug("메시지 생성 이벤트 수신 - messageId: {}", messageDto.id());

    Channel channel = channelRepository.findById(messageDto.channelId()).orElse(null);
    if (channel == null) {
      log.warn("채널을 찾을 수 없어 알림을 생성하지 않습니다. channelId: {}", messageDto.channelId());
      return;
    }

    String title = String.format("%s (#%s)", messageDto.author().username(), channel.getName());
    String content = messageDto.content();

    List<ReadStatus> readStatuses = readStatusRepository
        .findAllByChannelIdAndNotificationEnabledTrueWithUser(messageDto.channelId());

    for (ReadStatus readStatus : readStatuses) {
      User receiver = readStatus.getUser();
      // 메시지 작성자는 알림 대상에서 제외
      if (receiver.getId().equals(messageDto.author().id())) {
        continue;
      }
      Notification notification = new Notification(receiver, title, content);
      notificationRepository.save(notification);

      Cache cache = cacheManager.getCache("notifications");
      if (cache != null) {
        cache.evict(receiver.getId());
      }
      
      log.info("메시지 수신 알림 생성 완료 - receiver: {}, title: {}", receiver.getUsername(), title);
    }
  }

  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    log.debug("권한 변경 이벤트 수신 - userId: {}", event.userId());

    User user = userRepository.findById(event.userId()).orElse(null);
    if (user == null) {
      log.warn("사용자를 찾을 수 없어 권한 변경 알림을 생성하지 않습니다. userId: {}", event.userId());
      return;
    }

    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", event.oldRole(), event.newRole());

    Notification notification = new Notification(user, title, content);
    notificationRepository.save(notification);

    Cache cache = cacheManager.getCache("notifications");
    if (cache != null) {
      cache.evict(user.getId());
    }

    log.info("권한 변경 알림 생성 완료 - receiver: {}, content: {}", user.getUsername(), content);
  }
}
