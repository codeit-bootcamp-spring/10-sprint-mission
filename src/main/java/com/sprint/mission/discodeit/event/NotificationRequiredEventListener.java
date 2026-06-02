package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;

import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  /// 특정 채널에 메시지가 생성됐을때 알림 생성
  /// 특정 채널에서 메시지가 생성되고 DB에 저장후 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async
  public void on(MessageCreatedEvent event) {
    /// 해당 채널의 알림 여부를 활성화한 ReadStatus를 조회합니다.
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId());

    Message message = messageRepository.findByIdWithAuthorAndChannel(event.getMessageId())
            .orElseThrow(() -> MessageNotFoundException.withId(event.getMessageId()));

    /// 해당 ReadStatus의 사용자들에게 알림을 생성합니다.
    List<Notification> notifications = readStatuses.stream()
            /// 자기자신이 보낸 message는 알림이 안울려야하므로 필터링.
            .filter(readStatus -> !readStatus.getUser().getId().equals(message.getAuthor().getId()))
            .map(readStatus -> new Notification(
                    readStatus.getUser(), // 받는 사람
                    "%s (#%s)".formatted( // 제목
                            message.getAuthor().getUsername(),
                            "개인메시지"
                    ),
                    message.getContent()  // 내용
            ))
            .toList();

    notificationRepository.saveAll(notifications);
    log.debug("메시지 생성 알림 저장 완료: messageId={}, channelId={}, count={}",
            event.getMessageId(), event.getChannelId(), notifications.size());

  }

  /// 사용자 권한 변경시 알림 생성
  /// 권한변경이 commit 되고 실행.
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async
  public void on(RoleUpdatedEvent event) {

      User receiver = userRepository.findById(event.getUserId())
              .orElseThrow(() -> UserNotFoundException.withId(event.getUserId()));

      Notification notification = new Notification(
              receiver,
              "권한이 변경되었습니다.",
              "%s -> %s".formatted(event.getPreviousRole(), event.getNewRole())
      );

      notificationRepository.save(notification);
      log.debug("권한 변경 알림 저장 완료: userId={}, previousRole={}, newRole={}",
              event.getUserId(), event.getPreviousRole(), event.getNewRole());
    }

}
