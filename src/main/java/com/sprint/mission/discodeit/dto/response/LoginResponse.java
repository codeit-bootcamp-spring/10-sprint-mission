package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record LoginResponse(
    UUID userId,
    String username,
    String message
) {

  public static LoginResponse from(User user) {
    return new LoginResponse(
        user.getId(),
        user.getUsername(),
        "로그인 성공"
    );

  }
}
