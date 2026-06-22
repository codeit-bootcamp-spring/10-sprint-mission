package com.sprint.mission.discodeit.channel.infra.adapter;

import com.sprint.mission.discodeit.channel.domain.provider.ChannelNotifier;
import com.sprint.mission.discodeit.global.web.ws.stomp.constant.StompMessageType;
import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.publisher.AbstractStompPublisher;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChannelStompAdapter extends AbstractStompPublisher implements ChannelNotifier {

  public ChannelStompAdapter(
      SimpMessagingTemplate messagingTemplate) {
    super(messagingTemplate, WebSocketDestinations.SUB_PUBLIC_CHANNEL);
  }

  @Override
  protected void publish(UUID targetId, Object payload, StompMessageType type) {
    String destination = WebSocketDestinations.SUB_PRIVATE_CHANNEL.replace("{id}",
        targetId.toString());
    messagingTemplate.convertAndSend(destination, payload, Map.of("type", type));
  }
}
