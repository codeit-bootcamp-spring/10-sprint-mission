package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// 메시지 생성 시 해당 채널을 구독 중인 클라이언트에게 새 메시지를 발행하는 Listener
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

    private final MessageService messageService;

    // 서버에서 특정 STOMP destination으로 메시지를 발행할 때 사용하는 Spring 메시지 템플릿
    private final SimpMessagingTemplate messagingTemplate;

    // 메시지 생성 트랜잭션 Commit 후 MessageDto를 조회해 채널 구독 Destination으로 전송
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        MessageDto messageDto = messageService.find(event.getMessageId());

        // 해당 채널 메시지 destination을 구독 중인 클라이언트에게 새 메시지를 전송
        messagingTemplate.convertAndSend(
                "/sub/channels." + event.getChannelId() + ".messages",
                messageDto
        );
    }
}
