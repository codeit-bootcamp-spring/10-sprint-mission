package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
        @Schema(description = "변경할 User 이름", example = "dustle-updated")
        @Size(min = 1, max = 50, message = "newUsername은 1자 이상 50자 이하여야 합니다.")
        String newUsername,
        @Schema(description = "변경할 User 이메일", example = "dustle-updated@email.com")
        @Email(message = "newEmail 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "newEmail은 100자 이하여야 합니다.")
        String newEmail,
        @Schema(description = "변경할 User 비밀번호", example = "5678")
        @Size(min = 4, max = 60, message = "newPassword는 4자 이상 60자 이하여야 합니다.")
        String newPassword
) {}
