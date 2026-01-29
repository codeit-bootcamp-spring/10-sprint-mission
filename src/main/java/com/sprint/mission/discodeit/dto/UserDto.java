package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    // 생성 요청 DTO
    public record UserCreateRequest(
            String name,
            String email,
            String password,
            String filePath
    ) {}

    // 수정 요청 DTO
    public record UserUpdateRequest(
            String name,
            String email,
            String filepath,
            UUID contentId
    ) {}

    // 응답 DTO -- 온라인 상태 포함, 패스워드 제외
    public record UserResponse(
            UUID id,
            String name,
            String email,
            boolean isOnline, // UserStatus 기반 온라인 여부
            Instant createdAt
    ) {
        public static UserResponse from(User user, UserStatus status) {
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    status.isOnline(),
                    user.getCreatedAt()
            );
        }
    }
}
