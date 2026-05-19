package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.UUID;

@Component("messageChecker")
@RequiredArgsConstructor
public class MessageSecurityChecker {
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public boolean isMyMessage(UUID messageId, DiscodeitUserDetails userDetails){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        if(userDetails == null || userDetails.getUserDto() == null){
            return false;
        }
        return userDetails.getUserDto().getId().equals(message.getAuthor().getId());
    }
}
