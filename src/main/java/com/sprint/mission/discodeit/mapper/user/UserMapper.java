package com.sprint.mission.discodeit.mapper.user;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    // Entity -> DTO
    public UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                new UserStatusResponse(userStatus.isOnline())
        );
    }

    // DTO -> Entity
    public User toEntity(UserCreateRequest request, UUID profileImageID){
        return new User(
                request.name(),
                request.email(),
                request.password(),
                profileImageID
        );
    }
}
