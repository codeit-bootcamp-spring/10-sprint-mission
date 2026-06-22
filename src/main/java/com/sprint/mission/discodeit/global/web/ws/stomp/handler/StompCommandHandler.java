package com.sprint.mission.discodeit.global.web.ws.stomp.handler;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandHandler {

  boolean canHandle(StompCommand command);

  void handle(StompHeaderAccessor accessor);
}
