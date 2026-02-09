package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, boolean isOnline) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                isOnline,
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
