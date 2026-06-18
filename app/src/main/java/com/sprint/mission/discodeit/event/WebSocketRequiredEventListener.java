package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async
  public void handleMessage(MessageCreatedEvent event) {
    log.debug("웹소켓 메세지 수신");
    MessageDto messageDto = event.messageCreatedPayload().messageDto();
    messagingTemplate.convertAndSend(
        "/sub/channels." + messageDto.channelId() + ".messages",
        messageDto);
  }
}
