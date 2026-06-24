package com.sprint.mission.discodeit.websocket.event;

import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    Message message = event.message();
    MessageDto messageDto = messageMapper.toDto(message);

    messagingTemplate.convertAndSend(
        "/sub/channels." + messageDto.channelId() + ".messages",
        messageDto);
  }

}
