package com.sprint.mission.discodeit.message.securitychecker;

import com.sprint.mission.discodeit.message.repository.JPAMessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSecurityChecker {

  private final JPAMessageRepository jpaMessageRepository;

  public boolean isAuthor(UUID messageId, UUID currentUserId) {
    return jpaMessageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(currentUserId))
        .orElse(false);
  }


}
