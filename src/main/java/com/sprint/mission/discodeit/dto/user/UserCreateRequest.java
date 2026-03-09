package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "유저 생성 요청")
public record UserCreateRequest(
    @NotBlank
    @Schema(description = "유저 이름", example = "woody")
    String username,
    @Email
    @Schema(description = "유저 이메일", example = "woody@codeit.com")
    String email,
    @NotBlank
    @Schema(description = "유저 비밀번호", example = "1234")
    String password
) {

}
