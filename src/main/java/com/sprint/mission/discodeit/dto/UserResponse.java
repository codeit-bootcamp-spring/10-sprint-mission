package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    // password는 제외
    private UUID profileImageId;  // BinaryContent ID
    // 온라인 상태 정보 포함
    private String status;        // ONLINE, OFFLINE, AWAY 등
    private Instant lastActiveAt;
    private Instant createdAt;
    private Instant updatedAt;

    // Entity -> DTO 변환 메서드
    public static UserResponse from(User user, UserStatus userStatus) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageId(user.getProfileImageId())
                .status(userStatus != null ? userStatus.getStatus() : "OFFLINE")
                .lastActiveAt(userStatus != null ? userStatus.getLastActiveAt() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserResponse from(User user) {
        return from(user, null);  // UserStatus null로 전달
    }
}
