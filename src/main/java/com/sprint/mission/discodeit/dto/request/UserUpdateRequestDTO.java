package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequestDTO(
   String newUsername,

   String newEmail,

   String newPassword
) {}
