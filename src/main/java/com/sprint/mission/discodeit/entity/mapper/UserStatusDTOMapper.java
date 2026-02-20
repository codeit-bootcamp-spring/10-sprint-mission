package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusDTOMapper {

    public UserStatus userStatusRequestToUS(UserStatusRequestDTO req) {
        return new UserStatus(req.userID());
    }

    public UserStatusResponseDTO userStatusToResponse(UserStatus userStatus) {
        return new UserStatusResponseDTO(userStatus.getId(), userStatus.getUserID(),
            userStatus.getLastActiveAt());
    }

}
