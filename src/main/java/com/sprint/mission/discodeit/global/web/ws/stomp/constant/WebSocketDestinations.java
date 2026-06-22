package com.sprint.mission.discodeit.global.web.ws.stomp.constant;

import com.sprint.mission.discodeit.global.web.ws.constant.WebSocketConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebSocketDestinations {

  public static final String PUB_MESSAGE = "/channels/{id}/messages";

  public static final String SUB_MESSAGE = WebSocketConstants.SUB_PREFIX + "/channels/{id}/messages";
  public static final String SUB_ALL_USER = WebSocketConstants.SUB_PREFIX + "/users";
  public static final String SUB_NOTIFICATION = WebSocketConstants.SUB_PREFIX + "/users/{id}/notifications";
  public static final String SUB_PUBLIC_CHANNEL = WebSocketConstants.SUB_PREFIX + "/channels";
  public static final String SUB_PRIVATE_CHANNEL = WebSocketConstants.SUB_PREFIX + "/users/{id}/channels";
}
