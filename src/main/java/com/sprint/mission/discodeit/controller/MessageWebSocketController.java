package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void create(@Valid @Payload MessageCreateRequest request, Principal principal) {
    MessageCreateRequest authenticatedRequest = withAuthenticatedAuthor(request, principal);
    messageService.create(authenticatedRequest, List.of());
  }

  private MessageCreateRequest withAuthenticatedAuthor(
      MessageCreateRequest request,
      Principal principal
  ) {
    if (!(principal instanceof Authentication authentication)
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      throw new AccessDeniedException("Authentication is required to create a message.");
    }
    return new MessageCreateRequest(
        request.content(),
        request.channelId(),
        userDetails.getUserDto().id()
    );
  }
}
