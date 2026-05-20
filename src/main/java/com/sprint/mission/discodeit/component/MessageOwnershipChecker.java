package com.sprint.mission.discodeit.component;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageOwnershipChecker {

  private final MessageRepository messageRepository;

  public boolean isOwner(UUID messageId, Authentication authentication) {
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    return messageRepository.findById(messageId)
        .filter(message -> message.getAuthor().getId().equals(principal.getUserDto().id()))
        .isPresent();
  }
}
