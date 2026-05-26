package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageAccessService")
@RequiredArgsConstructor
public class MessageAccessService {
  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.existsByIdAndAuthorId(messageId, userId);
  }
}
