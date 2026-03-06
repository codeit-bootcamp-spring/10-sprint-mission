package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.repository.BinaryContentRepository;
//import com.sprint.mission.discodeit.service.UserStatusService;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;

//@Component
public class UserDTOMapper {

    public static UserDto userToResponse(User user, boolean online) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            BinaryContentDTOMapper.binaryContentToResponse(user.getProfile()),
            online);
    }

    public static User regtoUser(UserCreateRequestDTO req, BinaryContent profile) {
        return new User(
            req.username(),
            req.email(),
            req.password(),
            profile);
    }


}
