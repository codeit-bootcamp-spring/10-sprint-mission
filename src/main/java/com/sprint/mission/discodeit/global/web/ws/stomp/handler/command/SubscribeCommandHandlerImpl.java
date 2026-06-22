package com.sprint.mission.discodeit.global.web.ws.stomp.handler.command;

import com.sprint.mission.discodeit.global.web.ws.stomp.handler.StompCommandHandler;
import com.sprint.mission.discodeit.global.web.ws.stomp.handler.SubscribeCommandHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeCommandHandlerImpl implements StompCommandHandler {

  private final List<SubscribeCommandHandler> handlers;

  @Override
  public boolean canHandle(StompCommand command) {
    return StompCommand.SUBSCRIBE.equals(command);
  }

  @Override
  public void handle(StompHeaderAccessor accessor) {
    String destination = accessor.getDestination();
    for (SubscribeCommandHandler handler : handlers) {
      if (handler.canSupport(destination)) {
        handler.validate(accessor);
        return;
      }
    }
  }
}
