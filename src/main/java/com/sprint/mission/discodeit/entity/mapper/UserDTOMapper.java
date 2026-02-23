package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserDTOMapper {

    public UserResponseDTO userToResponse(User user, UserStatus userStatus) {
        return new UserResponseDTO(
            user.getId(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getUsername(),
            user.getEmail(),
            user.getProfileID(),
            userStatus.isOnline()
        );
    }

    public User regtoUser(UserCreateRequestDTO req, UUID profileId) {
        return new User(req.username(), req.email(), req.password(), profileId);
    }


}
