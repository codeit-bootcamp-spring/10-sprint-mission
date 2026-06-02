package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageAuth")
@RequiredArgsConstructor
public class MessageAuthorization {
  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.existsByIdAndAuthor_Id(messageId, userId);
  }
}
