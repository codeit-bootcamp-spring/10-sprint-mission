package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "사용자 정보 응답 DTO")
public record UserDto(
        @Schema(description = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "사용자 이름", example = "달선")
        String username,

        @Schema(description = "이메일 주소", example = "dalsun@naver.com")
        String email,

        @Schema(description = "프로필 이미지(바이너리)", example = "3f3fb215-32aa-4281-904c-45b9dd8b96fb")
        BinaryContentDto profile,

        @Schema(description = "현재 접속 여부", example = "true")
        Boolean online,

        @Schema(description = "권한", example = "USER")
        Role role
) {

}
