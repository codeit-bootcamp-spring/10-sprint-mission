package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public class UserStatusDto {
    public record Create(
            UUID userId
    ) {}

    public record Response(
            UUID userStatusId,
            UUID userId
    ) {
        public static Response of(UserStatus status) {
            return new Response(
                    status.getId(),
                    status.getUserId()
            );
        }
    }

    public record Update(
            UUID userStatusId,
            UUID userId
    ) {}
}
