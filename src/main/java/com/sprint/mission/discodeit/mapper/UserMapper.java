package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

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

    public static User toUser(UserCreateInfo userInfo) {
        return new User(
                userInfo.userName(),
                userInfo.password(),
                userInfo.email()
        );
    }
}
