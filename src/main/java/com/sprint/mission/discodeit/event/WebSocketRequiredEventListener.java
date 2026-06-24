package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// 메시지 생성 이벤트를 수신하여 WebSocket 구독자에게 실시간으로 브로드캐스트하는 이벤트 리스너
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  // STOMP 브로커를 통해 특정 destination으로 메시지를 전송하는 Spring 제공 템플릿
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  // MessageCreatedEvent 수신 시 WebSocket 브로드캐스트를 수행
  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    // WebSocket으로 내려보낼 messageDto 조회
    MessageDto messageDto = messageService.find(event.messageId());
    String destination = "/sub/channels." + event.channelId() + ".messages";
    // convertAndSend()
    // - Dto를 JSON으로 직렬화
    // -> SimpleBroker를 통해 destination 구독자 목록 조회 (doSend()로 위임함)
    // -> 각 클라이언트의 WebSocket 세션으로 STOMP MESSAGE 프레임 전송
    messagingTemplate.convertAndSend(destination, messageDto);
    log.debug("WebSocket 브로드캐스트 완료: destination={}, messageId={}",
        destination, event.messageId());
  }
}
