package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateInfo;
import com.sprint.mission.discodeit.dto.user.UserInfo;
import com.sprint.mission.discodeit.dto.user.UserInfoWithProfile;
import com.sprint.mission.discodeit.dto.user.UserInfoWithStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserMapper {
    private UserMapper() {
    }

    public static UserInfo toUserInfo(User user) {
        return new UserInfo(
                user.getId(),
                user.getUserName(),
                user.getEmail()
        );
    }

    public static UserInfoWithProfile toUserInfoWithProfile(User user) {
        return new UserInfoWithProfile(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getProfileId()
        );
    }

    public static UserInfoWithStatus toUserInfoWithStatus(User user, UserStatus userStatus) {
        return new UserInfoWithStatus(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
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
