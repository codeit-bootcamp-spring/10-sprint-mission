package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    /// 메시지가 생성되면 해당 엔드포인트로 메시지를 보내는 컴포넌트 구현.
    /// MessageService.create()내 MessageCreatedEvent를 받는다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {

        MessageDto messageDto = messageRepository.findByIdWithAuthorAndChannel(event.getMessageId())
                .map(messageMapper::toDto)
                .orElseThrow(() -> MessageNotFoundException.withId(event.getMessageId()));

        messagingTemplate.convertAndSend(
                "/sub/channels." + event.getChannelId() + ".messages",
                messageDto
        );
    }
}
