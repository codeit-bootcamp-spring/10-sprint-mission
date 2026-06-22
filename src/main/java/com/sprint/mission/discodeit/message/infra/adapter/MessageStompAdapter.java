package com.sprint.mission.discodeit.message.infra.adapter;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.publisher.AbstractStompPublisher;
import com.sprint.mission.discodeit.message.domain.provider.MessageNotifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageStompAdapter
    extends AbstractStompPublisher implements MessageNotifier {

  public MessageStompAdapter(
      SimpMessagingTemplate messagingTemplate) {
    super(messagingTemplate, WebSocketDestinations.SUB_MESSAGE);
  }
}
