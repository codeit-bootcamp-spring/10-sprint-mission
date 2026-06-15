package com.sprint.mission.discodeit.auth.application.event;

import com.sprint.mission.discodeit.auth.application.service.UserCredentialService;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

  private final UserCredentialService service;

  @EventListener
  public void handleUserCreatedEvent(UserCreatedEvent event) {
    service.create(event.id(), event.password());
  }

  @EventListener
  public void handleUserUpdatedEvent(UserUpdatedEvent event) {
    service.update(event.id(), event.password());
  }
}
