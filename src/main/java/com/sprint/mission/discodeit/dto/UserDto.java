package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.UUID;

public class UserDto {
    @Schema(name = "UserCreateRequest", description = "User 생성 정보")
    public record CreateRequest(
            @NotBlank(message = "사용자명은 필수입니다.")
            @Pattern(regexp = "^\\S+$", message = "사용자 이름에 공백을 포함할 수 없습니다.")
            String username,

            @Email @NotBlank(message = "이메일은 필수입니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
            @Pattern(regexp = "^\\S+$", message = "비밀번호에 공백을 포함할 수 없습니다.")
            String password
    ) {}

    @Schema(name = "UserResponse", description = "User 응답 정보")
    public record Response(
            UUID id,
            String username,
            String email,
            BinaryContentDto.Response profile,   // 이미지 ID
            boolean online,
            Role role
    ) {}

    @Schema(name = "UserUpdateRequest", description = "수정할 User 정보")
    public record UpdateRequest(
            @Pattern(regexp = "^\\S*$", message = "새 사용자 이름에 공백을 포함할 수 없습니다.")
            String newUsername,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            String newEmail,

            @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
            @Pattern(regexp = "^\\S*$", message = "새 비밀번호에 공백을 포함할 수 없습니다.")
            String newPassword
    ) {}
}
