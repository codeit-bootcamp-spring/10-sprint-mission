package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Role role,
    Boolean online
) {

  public record UserCreateRequest(
      @NotBlank String username,
      @NotBlank String password,
      @NotBlank @Email String email
  ) {

  }

  public record UserUpdateRequest(
      String newUsername,
      String newPassword,
      @Email String newEmail
  ) {

  }

  public record UserRoleUpdateRequest(
      UUID userId,
      Role newRole
  ) {

  }
}
