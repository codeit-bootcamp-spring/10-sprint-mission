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
    private String name;
    private String email;
    private UUID profileImageId;
    private List<UUID> messageList;
    private List<UUID> channelList;
    private List<UUID> friendsList;
    private boolean online;

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profileImageId=" + profileImageId +
                ", messageList=" + messageList +
                ", channelList=" + channelList +
                ", friendsList=" + friendsList +
                ", online=" + online +
                '}';
    }
}
