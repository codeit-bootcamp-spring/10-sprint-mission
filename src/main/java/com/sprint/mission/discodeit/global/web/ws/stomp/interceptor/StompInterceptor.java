package com.sprint.mission.discodeit.global.web.ws.stomp.interceptor;

import com.sprint.mission.discodeit.global.web.ws.stomp.handler.StompCommandHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

  private final List<StompCommandHandler> handlers;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand command = accessor.getCommand();
    if (command == null) {
      return message;
    }
    for (StompCommandHandler handler : handlers) {
      if (handler.canHandle(command)) {
        handler.handle(accessor);
        break;
      }
    }
    return message;
  }
}
