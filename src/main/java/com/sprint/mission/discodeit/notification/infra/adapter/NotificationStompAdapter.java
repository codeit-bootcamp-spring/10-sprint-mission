package com.sprint.mission.discodeit.notification.infra.adapter;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.publisher.AbstractStompPublisher;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationNotifier;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationStompAdapter
    extends AbstractStompPublisher implements NotificationNotifier {

  public NotificationStompAdapter(
      SimpMessagingTemplate messagingTemplate) {
    super(messagingTemplate, WebSocketDestinations.SUB_NOTIFICATION);
  }

  @Override
  public void notifyUpdated(UUID targetId, Object payload) {
    throw new UnsupportedOperationException("updated not supported");
  }
}
