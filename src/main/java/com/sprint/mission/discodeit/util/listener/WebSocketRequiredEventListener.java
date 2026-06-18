package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    UUID channelId = event.data().channelId();
    String destination = String.format("/sub/channels.%s.messages", channelId);

    messagingTemplate.convertAndSend(destination, event.data());
  }

}
