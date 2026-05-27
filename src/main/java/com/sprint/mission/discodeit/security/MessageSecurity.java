package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException());

        return message.getAuthor().getId().equals(userId);
    }
}
