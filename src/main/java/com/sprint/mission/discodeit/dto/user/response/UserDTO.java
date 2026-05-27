package com.sprint.mission.discodeit.dto.user.response;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "사용자 정보 응답")
public record UserDTO(
        @Schema(description = "사용자 고유 식별자(ID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "사용자 이름", example = "승주")
        String username,

        @Schema(description = "사용자 이메일", example = "seungju@example.com")
        String email,

        @Schema(description = "프로필")
        BinaryContentDTO profile,

        @Schema(description = "온라인 여부", example = "true")
        boolean online,

        @Schema(description = "권한 정보", example = "USER")
        Role role
) {}