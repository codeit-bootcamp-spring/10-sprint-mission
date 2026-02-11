package com.sprint.mission.discodeit.userstatus.mapper;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusInfo;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;

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
