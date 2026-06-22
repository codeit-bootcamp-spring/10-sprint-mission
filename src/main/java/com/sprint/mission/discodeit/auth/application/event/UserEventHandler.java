package com.sprint.mission.discodeit.auth.application.event;

import com.sprint.mission.discodeit.auth.application.service.UserCredentialService;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

  private final UserCredentialService service;

  @EventListener
  public void handleUserCreatedEvent(UserCreatedEvent event) {
    service.create(event.getId(), event.getPassword());
  }

  @EventListener
  public void handleUserUpdatedEvent(UserUpdatedEvent event) {
    if (StringUtils.hasText(event.getPassword())) {
      service.update(event.getId(), event.getPassword());
    }
  }

  @EventListener
  public void handleUserDeletedEvent(UserDeletedEvent event) {
    service.deleteByUserId(event.getId());
  }
}
