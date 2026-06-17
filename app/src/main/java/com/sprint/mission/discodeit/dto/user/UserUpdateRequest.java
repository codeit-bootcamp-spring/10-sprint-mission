package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "유저 수정 요청")
public record UserUpdateRequest(
    @Size(min = 1)
    @Schema(description = "새로운 유저 이름", example = "buzz")
    String newUsername,
    @Email
    @Schema(description = "새로운 유저 이메일", example = "buzz@codeit.com")
    String newEmail,
    @Size(min = 1)
    @Schema(description = "새로운 유저 비밀번호", example = "1234")
    String newPassword
) {

}
