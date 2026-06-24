package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
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

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    // MessageCreatedEvent 안에 이미 MessageDto가 들어있습니다.
    MessageDto message = event.getData();

    String destination = "/sub/channels." + message.channelId() + ".messages";

    // 해당 채널을 구독 중인 클라이언트들에게 새 메시지를 전송합니다.
    messagingTemplate.convertAndSend(destination, message);

    log.debug("웹소켓 메시지 전송 완료: destination={}, messageId={}", destination, message.id());
  }
}
