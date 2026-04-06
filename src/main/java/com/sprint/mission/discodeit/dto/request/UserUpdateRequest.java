package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
    @Pattern(regexp = "^(?!\\s*$).+", message = "newUsername은 공백일 수 없습니다.")
    @Size(max = 50, message = "newUsername은 50자 이하여야 합니다.")
    String newUsername,
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Size(max = 100, message = "newEmail은 100자 이하여야 합니다.")
    String newEmail,
    @Pattern(regexp = "^(?!\\s*$).+", message = "newPassword는 공백일 수 없습니다.")
    @Size(max = 60, message = "newPassword는 60자 이하여야 합니다.")
    String newPassword
) {

}
