package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  // 새 메세지가 생성되면, 해당채널 알림 생성
  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    Message message =
        messageRepository
            .findById(event.messageId())
            .orElseThrow(() -> MessageNotFoundException.withId(event.messageId()));

    UUID channelId = message.getChannel().getId();
    UUID authorId = message.getAuthor().getId();

    List<ReadStatus> readStatuses =
        readStatusRepository.findAllNotificationEnabledByChannelId(channelId);

    String channelName = message.getChannel().getName();
    String authorName = message.getAuthor().getUsername();

    String title = authorName + " (#" + channelName + ")";
    String content = message.getContent();

    for (ReadStatus readStatus : readStatuses) {
      UUID receiverId = readStatus.getUser().getId();

      // 메시지를 보낸 사람에게는 자기 메시지 알림을 보내지 않습니다.
      if (receiverId.equals(authorId)) {
        continue;
      }

      notificationService.create(receiverId, title, content);
    }
  }

  // 권한이 변경된 사용자에게 권한 변경 알림.
  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = event.previousRole() + " -> " + event.newRole();

    notificationService.create(event.receiverId(), title, content);
  }
}
