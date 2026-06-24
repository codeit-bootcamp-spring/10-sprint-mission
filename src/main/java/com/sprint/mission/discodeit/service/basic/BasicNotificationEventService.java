package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationEventService;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 도메인 이벤트 기반 알림 발송 정책의 기본 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationEventService implements NotificationEventService {

  private final NotificationService notificationService;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public void sendByMessageCreated(UUID messageId) {
    Message message = messageRepository.findById(messageId).orElseThrow();
    Channel channel = message.getChannel();
    User author = message.getAuthor();

    List<ReadStatus> targetReadStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(channel.getId(), true);

    String title = String.format("%s (#%s)", author.getUsername(), channel.getName());
    String content = message.getContent();

    targetReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(author.getId()))
        .forEach(user -> notificationService.create(user.getId(), title, content));
  }

  @Transactional
  @Override
  public void sendByRoleUpdated(UUID userId, Role oldRole, Role newRole) {
    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", oldRole, newRole);
    notificationService.create(userId, title, content);
  }

  @Transactional
  @Override
  public void sendAsyncErrorNotification(String requestId, String methodName, String errorMessage) {
    String messageContent = String.format(
            """
            RequestId: %s
            Method: %s
            Error: %s
            """,
        requestId, methodName, errorMessage
    );

    userRepository.findByRole(Role.ADMIN).forEach(admin -> 
      notificationService.create(admin.getId(), "시스템 비동기 에러 발생", messageContent)
    );
  }
}
