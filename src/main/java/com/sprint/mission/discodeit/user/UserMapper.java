package com.sprint.mission.discodeit.user;

import com.sprint.mission.discodeit.userstatus.UserStatus;
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

    public static User toUser(UserCreateInfo userInfo) {
        return new User(
                userInfo.userName(),
                userInfo.password(),
                userInfo.email()
        );
    }
}
