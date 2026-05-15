package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 응답")
public record UserDto(
    @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID id,
    @Schema(description = "유저 이름", example = "woody")
    String username,
    @Schema(description = "유저 이메일", example = "woody@codeit.com")
    String email,
    @Schema(description = "유저 프로필 사진")
    BinaryContentDto profile,
    @Schema(description = "유저 온라인 상태", example = "true")
    boolean online,
    @Schema(description = "유저 권한", example = "USER")
    Role role
) {

}
