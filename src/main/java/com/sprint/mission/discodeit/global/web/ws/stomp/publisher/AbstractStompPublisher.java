package com.sprint.mission.discodeit.global.web.ws.stomp.publisher;

import com.sprint.mission.discodeit.common.payload.notifier.DomainNotifier;
import com.sprint.mission.discodeit.common.payload.notifier.TargetDomainNotifier;
import com.sprint.mission.discodeit.global.web.ws.stomp.constant.StompMessageType;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
public abstract class AbstractStompPublisher implements DomainNotifier, TargetDomainNotifier {

  protected final SimpMessagingTemplate messagingTemplate;
  private final String destination;

  protected void publish(Object payload, StompMessageType type) {
    messagingTemplate.convertAndSend(destination, payload, Map.of("type", type));
  }

  protected void publish(UUID targetId, Object payload, StompMessageType type) {
    String destination = this.destination.replace("{id}", targetId.toString());
    messagingTemplate.convertAndSend(destination, payload, Map.of("type", type));
  }

  @Override
  public void notifyCreated(Object payload) {
    publish(payload, StompMessageType.CREATED);
  }

  @Override
  public void notifyUpdated(Object payload) {
    publish(payload, StompMessageType.UPDATED);
  }

  @Override
  public void notifyDeleted(Object payload) {
    publish(payload, StompMessageType.DELETED);
  }

  @Override
  public void notifyCreated(UUID targetId, Object payload) {
    publish(targetId, payload, StompMessageType.CREATED);
  }

  @Override
  public void notifyUpdated(UUID targetId, Object payload) {
    publish(targetId, payload, StompMessageType.UPDATED);
  }

  @Override
  public void notifyDeleted(UUID targetId, Object payload) {
    publish(targetId, payload, StompMessageType.DELETED);
  }
}
