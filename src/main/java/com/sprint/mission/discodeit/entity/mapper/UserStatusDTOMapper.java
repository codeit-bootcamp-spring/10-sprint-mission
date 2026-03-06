package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusDTOMapper {

    public static UserStatusDto userStatusToResponse(UserStatus userStatus) {
        return new UserStatusDto(
            userStatus.getId(),
            userStatus.getUser().getId(),
            userStatus.getLastActiveAt());
    }

}
