package com.sprint.mission.discodeit.global.web.ws.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebSocketConstants {

  public static final String ENDPOINT = "/ws";
  public static final String WS_MATCHER = ENDPOINT + "/**";
  public static final String PUB_PREFIX = "/pub";
  public static final String SUB_PREFIX = "/sub";
}
