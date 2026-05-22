package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityExpression")
@RequiredArgsConstructor
public class SecurityExpressionService {

  private final MessageRepository messageRepository;

  public boolean isSelf(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication ==
        null || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }

    return userDetails.getUserDto().id().equals(userId);
  }

  public boolean isMessageAuthor(UUID messageId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication ==
        null || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }

    Message message = messageRepository.findById(messageId).orElse(null);

    if (message == null || message.getUserId() == null) {
      return false;
    }

    return message.getUserId().equals(userDetails.getUserDto().id());
  }

}
