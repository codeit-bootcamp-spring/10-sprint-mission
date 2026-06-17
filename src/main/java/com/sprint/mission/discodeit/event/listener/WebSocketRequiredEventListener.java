package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRequiredEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public void handleMessage(MessageCreatedEvent event){
        UUID messageId = event.getMessageId();
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new MessageNotFoundException(messageId)
        );

        String destination = "/sub/channels." + event.getChannelId() + ".messages";
        MessageDto dto = messageMapper.toDto(message);
        log.info("[WebSocket 브로드캐스팅] 경로: {}, 메시지 ID: {}", destination, event.getMessageId());
        messagingTemplate.convertAndSend(destination, dto);
    }
}
