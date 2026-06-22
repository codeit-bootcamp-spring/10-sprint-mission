package com.sprint.mission.discodeit.auth.application.event;

import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.auth.application.service.UserCredentialService;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserEventHandlerTest {

  @Mock
  private UserCredentialService service;

  @InjectMocks
  private UserEventHandler handler;

  @Test
  @DisplayName("handleUserCreatedEvent - 이벤트를 수신하여 UserCredential을 생성한다.")
  void handleUserCreatedEvent() {
    UUID id = UUID.randomUUID();
    UserCreatedEvent event = new UserCreatedEvent(id, "password");

    handler.handleUserCreatedEvent(event);

    verify(service).create(id, "password");
  }

  @Test
  @DisplayName("handleUserUpdatedEvent - 이벤트를 수신하여 UserCredential을 업데이트한다.")
  void handleUserUpdatedEvent() {
    UUID id = UUID.randomUUID();
    UserUpdatedEvent event = new UserUpdatedEvent(id, "new-password");

    handler.handleUserUpdatedEvent(event);

    verify(service).update(id, "new-password");
  }
}
