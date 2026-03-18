package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "유저 DTO")
public record UserDto(
        @Schema(description = "유저 ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        UUID id,
        @Schema(description = "유저명", example = "dustle")
        String username,
        @Schema(description = "이메일", example = "dustle@email.com")
        String email,
        @Schema(description = "프로필")
        BinaryContentDto profile,
        @Schema(description = "온라인 여부", example = "true")
        Boolean online
) {
}
