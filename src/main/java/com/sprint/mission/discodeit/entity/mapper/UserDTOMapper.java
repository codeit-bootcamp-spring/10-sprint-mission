package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserDTOMapper {

    public UserResponseDTO userToResponse (User user, UserStatus userStatus) {
        return new UserResponseDTO(user.getId(),
                user.getProfileID(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public User regtoUser(UserCreateRequestDTO req, UUID profileId){
        return new User(req.userName(), req.email(), req.password(), profileId);
    }



}
