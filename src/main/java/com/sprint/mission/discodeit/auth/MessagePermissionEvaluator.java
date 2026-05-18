package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessagePermissionEvaluator {

    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId, DiscodeitUserDetails userDetails) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthor() != null &&
                        message.getAuthor().getId().equals(userDetails.getUserDto().id()))
                .orElse(false);
    }

}
