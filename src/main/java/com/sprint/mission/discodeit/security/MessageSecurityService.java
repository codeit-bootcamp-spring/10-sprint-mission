package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageSecurityService")
@RequiredArgsConstructor
public class MessageSecurityService {

    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
                .map(m -> m.getAuthor().getId().equals(userId))
                .orElse(true);
    }
}
