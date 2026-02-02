package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.USRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.USResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusDTOMapper {

    public UserStatus userStatusRequestToUS(USRequestDTO req){
        return new UserStatus(req.userID());
    }

    public USResponseDTO userStatusToResponse(UserStatus userStatus){
        return new USResponseDTO(userStatus.getId(), userStatus.getUserID(), userStatus.getLastActiveAt());
    }

}
