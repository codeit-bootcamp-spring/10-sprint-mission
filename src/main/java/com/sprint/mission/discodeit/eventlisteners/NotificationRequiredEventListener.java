package com.sprint.mission.discodeit.eventlisteners;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

// 알림 관련 이벤트를 처리하는 리스너
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  // 메시지 생성 이벤트가 발행했을 때 처리하는 메서드
  @Async("eventTaskExecutor")
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW) // 전파 단계 = 새로운 스레드를 생성해서 진행
  public void on(MessageCreatedEvent event) {
    // 메시지 이벤트로부터 메시지 ID 추출 및 메시지 레포지토리에서 메시지 객체 가져옴
    Message message = messageRepository.findById(event.messageId())
        .orElseThrow(() -> new MessageNotFoundException(event.messageId()));

    // 채널, 작성자, ReadStatus 리스트를 Fetch
    Channel channel = message.getChannel();
    User author = message.getAuthor();
    // 알림 활성화된 사용자들의 상태 목록들만 가져옴
    List<ReadStatus> readStatuses =
        readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channel.getId());

    // readStatus 리스트를 순회하면서
    for (ReadStatus readStatus : readStatuses) {
      // 메시지 작성자와 알림 수신자가 같으면 그냥 무시
      User receiver = readStatus.getUser();
      if (receiver.getId().equals(author.getId())) {
        continue;
      }

      // 알림 레포지토리에 정보들을 담아 생성
      notificationRepository.save(new Notification(
          receiver,
          author.getUsername() + " (#" + channel.getName() + ")",
          message.getContent()
      ));
    }
  }
  
  @Async("eventTaskExecutor")
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(event.userId()));

    notificationRepository.save(new Notification(
        receiver,
        "권한이 변경되었습니다.",
        event.previousRole().name() + " -> " + event.newRole().name()
    ));
  }
}
