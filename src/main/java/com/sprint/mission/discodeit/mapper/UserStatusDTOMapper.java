package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusDTOMapper {

    public static UserStatusDto toDto(UserStatus userStatus) {
        return new UserStatusDto(
            userStatus.getId(),
            userStatus.getUser().getId(),
            userStatus.getLastActiveAt());
    }

}
