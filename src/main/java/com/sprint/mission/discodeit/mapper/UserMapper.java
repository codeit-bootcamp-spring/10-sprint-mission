package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserMapper {

    public static UserResponseDto toDto(User user, UserStatus userStatus) {
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

    public static User toEntity(UserCreateDto dto, BinaryContent profile) {
        return new User(dto.username(),dto.email(),dto.password(),profile==null?null:profile.getId());
    }
}
