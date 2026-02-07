package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDTO(
   String newUsername,

   @Email(message = "email 형식이 맞지 않습니다")
   String newEmail,

   String newPassword,

   ProfileCreateRequestDTO profileImage
) {}
