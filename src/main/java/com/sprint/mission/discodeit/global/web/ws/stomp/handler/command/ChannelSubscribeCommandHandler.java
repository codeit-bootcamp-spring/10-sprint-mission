package com.sprint.mission.discodeit.global.web.ws.stomp.handler.command;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.handler.SubscribeCommandHandler;
import com.sprint.mission.discodeit.global.web.ws.stomp.provider.ChannelSubscribeValidator;
import com.sprint.mission.discodeit.global.web.ws.stomp.util.StompDestinationParser;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ChannelSubscribeCommandHandler implements SubscribeCommandHandler {
  private final ChannelSubscribeValidator validator;

  @Override
  public boolean canSupport(String destination) {
    return destination.startsWith(WebSocketDestinations.PUB_MESSAGE);
  }

  @Override
  public void validate(StompHeaderAccessor accessor) {
    UUID userId = UUID.fromString(accessor.getUser().getName());
    UUID channelId = getChannelId(accessor);
    validator.verifyUserInChannel(channelId, userId);
  }

  private UUID getChannelId(StompHeaderAccessor accessor) {
    String destination = accessor.getDestination();
    return StompDestinationParser.extractChannelId(destination);
  }
}
