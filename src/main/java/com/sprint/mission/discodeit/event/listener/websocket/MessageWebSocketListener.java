package com.sprint.mission.discodeit.event.listener.websocket;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.event.MessageEvents;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 메시지 생성 이벤트를 감지하여 WebSocket 구독자들에게 실시간으로 브로드캐스트하는 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    /**
     * 메시지가 DB에 저장되고 트랜잭션이 커밋된 후에 호출됩니다.
     * 해당 채널을 구독 중인 모든 클라이언트에게 메시지 정보를 전송합니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageCreated(MessageEvents.Created event) {
        MessageDto.Response message = messageService.find(event.messageId());
        String destination = "/sub/channels." + message.channelId() + ".messages";
        
        log.debug("[WebSocket] 메시지 브로드캐스트: Destination={}, MessageID={}", destination, message.id());
        messagingTemplate.convertAndSend(destination, message);
    }
}
