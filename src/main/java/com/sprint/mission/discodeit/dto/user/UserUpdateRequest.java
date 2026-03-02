package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
        @Schema(description = "변경할 User 이름", example = "dustle-updated")
        String newUsername,
        @Schema(description = "변경할 User 이메일", example = "dustle-updated@email.com")
        String newEmail,
        @Schema(description = "변경할 User 비밀번호", example = "5678")
        String newPassword
) {}
