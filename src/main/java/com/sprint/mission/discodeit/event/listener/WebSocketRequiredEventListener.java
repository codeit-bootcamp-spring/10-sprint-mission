package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final MessageMapper messageMapper;

  // 데이터 정합성을 위해 DB 트랜잭션 커밋 이후에 실제 WebSocket을 전송해야 함
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    Message message = messageService.findById(event.messageId());
    MessageDto messageDto = messageMapper.toDto(message);

    String destination = "/sub/channels." + event.channelId() + ".messages";

    log.info(
        "Sending message created event to WebSocket subscribers - destination: {}, messageId: {}",
        destination,
        event.messageId()
    );

    // 해당 채널을 구독 중인 클라이언트들에게 메시지 전송
    messagingTemplate.convertAndSend(destination, messageDto);
  }
}
