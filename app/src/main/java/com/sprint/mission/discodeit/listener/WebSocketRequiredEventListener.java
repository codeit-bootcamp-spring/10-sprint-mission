package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.kafka.LocalMessageBroadcastEvent;
import com.sprint.mission.discodeit.listener.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final KafkaProducer kafkaProducer;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleOriginMessage(MessageCreatedEvent event) {
    log.debug("[WS] 메시지 생성: Kafka로 브로드캐스트 송출");
    kafkaProducer.broadcastMessage(event.messageDto());
  }

  @EventListener
  public void handleLocalBroadcast(LocalMessageBroadcastEvent event) {
    log.debug("[WS] 로컬 웹소켓 브로커를 통해 연결된 유저들에게 메시지 전송");
    messagingTemplate.convertAndSend(
        String.format("/sub/channels.%s.messages", event.messageDto().channelId()),
        event.messageDto()
    );
  }
}
