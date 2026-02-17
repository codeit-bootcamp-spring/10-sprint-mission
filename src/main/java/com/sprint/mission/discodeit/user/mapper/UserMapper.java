package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isOnline(),
                user.getProfileImage()
        );
    }
}
