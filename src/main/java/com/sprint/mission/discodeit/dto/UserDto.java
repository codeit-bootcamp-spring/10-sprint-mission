package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {

  public record UserLoginRequest(
      @NotBlank String username,
      @NotBlank String password) {

  }

  public record UserCreateRequest(
      @NotBlank String username,
      @NotBlank String password,
      @NotBlank @Email String email) {

  }

  public record UserUpdateRequest(String newUsername, String newPassword, @Email String newEmail) {

  }
}
