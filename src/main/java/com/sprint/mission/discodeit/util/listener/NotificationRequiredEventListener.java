package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ErrorNotificationEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    name = "notification-type.kafka.enabled",
    havingValue = "false",
    matchIfMissing = true //디폴트
)
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final SseService sseService;

  @Async("notificationTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    //채널이름, 보낸사람, 메세지 내용
    //활성화 리드스테이터스 조회
    //해당 사람들에 대해 알림 생성(본인 제외)
    MessageDto data = event.data();
    String title =
        data.author().username() + " (#" + (event.channelName() != null ? event.channelName() :
            ChannelType.PRIVATE) + ")";
    readStatusRepository.findAllByChannelIdAndNotificationEnabledAndUserIdNot(data.channelId(),
            true, data.author().id())
        .forEach(r -> {
          NotificationDto response = notificationService.create(
              new NotificationCreateRequest(r.getUser().getId(), title, data.content()));
          sseService.send(List.of(r.getUser().getId()), "notifications.created", response);
        });
  }

  @Async("notificationTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    //권한 변경 당사자에게 알림
    String title = "권한이 변경되었습니다.";
    String content = event.beforeRole() + " -> " + event.afterRole();
    NotificationDto response = notificationService.create(
        new NotificationCreateRequest(event.userId(), title, content));
    sseService.send(List.of(event.userId()), "notifications.created", response);
  }

  @Async("notificationTaskExecutor")
  @EventListener
  public void on(ErrorNotificationEvent event) {
    userRepository.findAllByRole(Role.ADMIN)
        .forEach(u -> {
          NotificationDto response = notificationService.create(new NotificationCreateRequest(
              u.getId(),
              event.title(),
              event.content()
          ));
          sseService.send(List.of(u.getId()), "notifications.created", response);
        });

  }

}
