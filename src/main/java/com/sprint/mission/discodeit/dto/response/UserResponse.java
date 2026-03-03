package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    Boolean online
) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        null // online 상태는 컨트롤러에서 별도 처리
    );
  }

  public static UserResponse from(UserDto userDto) {
    return new UserResponse(
        userDto.id(),
        userDto.createdAt(),
        userDto.updatedAt(),
        userDto.username(),
        userDto.email(),
        userDto.profileId(),
        userDto.online()
    );
  }
}
