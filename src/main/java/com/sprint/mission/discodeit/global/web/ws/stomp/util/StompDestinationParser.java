package com.sprint.mission.discodeit.global.web.ws.stomp.util;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.global.web.ws.stomp.exception.StompErrorCode;
import com.sprint.mission.discodeit.global.web.ws.stomp.exception.StompException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StompDestinationParser {

  public static UUID extractChannelId(String destination) {
    if (!StringUtils.hasText(destination) || !destination.startsWith(
        WebSocketDestinations.PUB_MESSAGE)) {
      throw new StompException(StompErrorCode.DESTINATION_INCORRECT_FORMAT, "destination",
          destination);
    }
    String stringId = destination.replace(WebSocketDestinations.PUB_MESSAGE, "")
        .substring(1);
    try {
      return UUID.fromString(stringId);
    } catch (IllegalArgumentException ex) {
      throw new StompException(StompErrorCode.INCORRECT_ID, "id", stringId);
    }
  }
}
