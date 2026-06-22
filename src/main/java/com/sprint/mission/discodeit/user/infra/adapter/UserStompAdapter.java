package com.sprint.mission.discodeit.user.infra.adapter;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.publisher.AbstractStompPublisher;
import com.sprint.mission.discodeit.user.domain.provider.UserNotifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserStompAdapter extends AbstractStompPublisher implements UserNotifier {

  public UserStompAdapter(SimpMessagingTemplate messagingTemplate) {
    super(messagingTemplate, WebSocketDestinations.SUB_ALL_USER);
  }
}
