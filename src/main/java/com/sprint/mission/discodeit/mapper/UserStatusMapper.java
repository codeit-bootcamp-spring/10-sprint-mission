package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusInfo;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserStatusMapper {
    private UserStatusMapper(){}

    public static UserStatusInfo toUserStatusInfo(UserStatus userStatus) {
        return new UserStatusInfo(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastOnlineAt(),
                userStatus.isOnline()
        );
    }


}
