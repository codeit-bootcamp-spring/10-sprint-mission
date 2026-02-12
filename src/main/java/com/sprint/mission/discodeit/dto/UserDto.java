package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    // 요청 DTO
    public record UserRequest(
            String name,
            String email,
            String password
    ) {}

    // 전체 호출 응답
    public record FindAllUserResponse(
            UUID userId,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,
            Boolean online
    ) {
        public static FindAllUserResponse from(User user, UserStatus userStatus){
            return new FindAllUserResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getName(),
                    user.getEmail(),
                    user.getBinaryContentId(),
                    userStatus.isOnline()
            );
        }
    }


    // 응답 DTO -- 온라인 상태 포함, 패스워드 제외
    public record UserResponse(
            UUID userId,
            String name,
            String email,
            boolean isOnline // UserStatus 기반 온라인 여부
    ) {
        public static UserResponse from(User user, UserStatus status) {
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    status.isOnline()
            );
        }
    }
}
