package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/*
    WebSocketRequiredEventListener
    ------------------------------
    메인 트랜잭션 성공적으로 커밋되었을 때만, 메시지를 전송하는 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;      // 웹소켓 메시지 전송

    private final MessageService messageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent messageCreatedEvent) {
        MessageDto messageDto = messageService.findById(messageCreatedEvent.messageId());

        // 송신지 설정
        String destination = String.format("/sub/channels.%s.messages", messageCreatedEvent.channelId());

        // 브로드캐스팅: 해당 채널에 포함된 사용자
        messagingTemplate.convertAndSend(destination, messageDto);

        log.info("[WEBSOCKET] 채널 {}에 실시간 메시지 브로드캐스팅 완료: messageId={}", messageCreatedEvent.channelId(), messageCreatedEvent.messageId());
    }
}