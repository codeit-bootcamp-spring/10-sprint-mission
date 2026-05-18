package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePermissionEvaluator {

  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId, UUID authorId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(authorId))
        .orElse(false);
  }
}
