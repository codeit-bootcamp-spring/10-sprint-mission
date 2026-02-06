package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusInfoDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

    public UserStatusInfoDto toUserInfoDto(UserStatus userStatus) {
        return new UserStatusInfoDto(userStatus.getId(),
        userStatus.getUserId(),
        userStatus.updateStatusType());
    }
}
