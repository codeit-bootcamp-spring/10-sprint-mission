package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    Role role,
    BinaryContentDto profile,
    Boolean online
) {
    public static UserDto of(
            User user,
            BinaryContentDto profile,
            boolean online
    ) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                profile,
                online
                );
    }
}
