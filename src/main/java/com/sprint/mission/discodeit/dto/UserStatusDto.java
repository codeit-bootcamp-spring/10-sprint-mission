package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public class UserStatusDto {

    public record UserStatusRequest(
            UUID userId
    ) {}

    public record UserStatusResponse(
            UUID userStatusId,
            UUID userId,
            UserStatus.Status status
    ){
        public static UserStatusResponse from(UserStatus userStatus) {
            return new UserStatusResponse(
                    userStatus.getId(),
                    userStatus.getUserId(),
                    userStatus.getStatus()
            );
        }
    }

}
