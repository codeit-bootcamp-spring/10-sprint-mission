package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.status.UserStatusRepository;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusResponse {
    private UUID id;
    private UUID userId;
    private String status;
    private Instant lastActiveAt;
    private Instant lastSeenAt;
    private boolean online;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserStatusResponse from(UserStatus userStatus){
        return UserStatusResponse.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUserId())
                .status(userStatus.getStatus())
                .lastActiveAt(userStatus.getLastActiveAt())
                .lastSeenAt(userStatus.getLastSeenAt())
                .online(userStatus.isOnline())
                .createdAt(userStatus.getCreatedAt())
                .updatedAt(userStatus.getUpdatedAt())
                .build();
    }
}
