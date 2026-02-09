package com.sprint.mission.discodeit.userstatus;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusInfo;

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
