package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.user.dto.UserCreateInfo;
import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.user.dto.UserInfoWithStatus;

public class UserMapper {
    private UserMapper() {
    }

    public static UserInfo toUserInfo(User user, UserStatus userStatus) {
        return new UserInfo(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getId()
        );
    }

    public static UserInfoWithStatus toUserInfoWithStatus(User user, UserStatus userStatus) {
        return new UserInfoWithStatus(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getId(),
                userStatus.isOnline()
        );
    }

    public static UserDto toUserDto(User user, UserStatus userStatus) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdateAt(),
                user.getUserName(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }

    public static User toUser(UserCreateInfo userInfo) {
        return new User(
                userInfo.userName(),
                userInfo.password(),
                userInfo.email()
        );
    }
}
