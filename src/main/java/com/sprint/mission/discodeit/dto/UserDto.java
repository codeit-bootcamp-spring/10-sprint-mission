package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public class UserDto {

    public record Create(
            String username,
            String email,
            String password,
            BinaryContentDto.Create profile
    ) {}

    public record Response(
            UUID id,
            String username,
            String email,
            UUID profileId,
            boolean isOnline,
            Instant lastActiveAt
    ) {
        public static Response of(User user, UserStatus status) {
            return new Response(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    status.isOnline(),
                    status.getLastActiveAt()
            );
        }
    }

    public record Update(
            UUID id,
            String username,
            String email,
            String password,
            BinaryContentDto.Create profile
    ) {}

    public record Login(
            String username,
            String password
    ) {}
}
