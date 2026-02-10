package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private UUID userId;
    private String username;
    private String email;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> messageList;
    private List<UUID> friendsList;
    private boolean online;

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "userId=" + userId +
                ", name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileImageId=" + profileId +
                ", messageList=" + messageList +
                ", friendsList=" + friendsList +
                ", online=" + online +
                '}';
    }
}
