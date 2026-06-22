package com.sprint.mission.discodeit.global.web.ws.stomp.provider;

import java.util.UUID;

public interface ChannelSubscribeValidator {

  void verifyUserInChannel(UUID channelId, UUID userId);
}
