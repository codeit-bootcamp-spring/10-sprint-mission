package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    //  User -> UserInfoDto
    public UserInfoDto userToUserInfoDto(User user, UserStatus userStatus){
        return new UserInfoDto(user.getName(),
                user.getId(),
                userStatus.getStatusType(),
                user.getEmail(),
                user.getProfileId());
    }
}
