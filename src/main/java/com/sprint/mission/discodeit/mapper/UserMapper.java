package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.NoSuchElementException;

public class UserMapper {

    public static UserResponseDto userToDto(User user, UserStatus userStatus) {
        return new UserResponseDto(
                user.getId()
                ,user.getUsername()
                ,user.getEmail()
                ,user.getProfileId()
                ,userStatus.isOnline()
                ,user.getCreatedAt()
                ,user.getUpdatedAt()
        );
    }
}
