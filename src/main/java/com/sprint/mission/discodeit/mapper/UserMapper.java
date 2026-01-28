package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserMapper {
    public static UserResponseDTO toResponse(User user, UserStatus status) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),

                status.getStatusType(),
                status.getLastLoginAt(),

                user.getProfileImageId()
        );
    }
}
