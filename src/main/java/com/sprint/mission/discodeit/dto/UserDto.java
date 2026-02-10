package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class UserDto {

    public record Create(
            String username,
            String email,
            String password,
            MultipartFile profile
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,
            boolean online
    ) {
        public static Response of(User user, UserStatus status) {
            return new Response(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    status.isOnline()
                    );
        }
    }

    public record Update(
            String username,
            String email,
            String password,
            MultipartFile profile
    ) {}

    public record Login(
            String username,
            String password
    ) {}
}
