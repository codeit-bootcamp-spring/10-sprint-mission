package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStateRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusDTOMapper {

    public UserStatus userStatusRequestToUS(UserStateRequestDTO req){
        return new UserStatus(req.userID());
    }

    public UserStateResponseDTO userStatusToResponse(UserStatus userStatus){
        return new UserStateResponseDTO(userStatus.getId(), userStatus.getUserID(), userStatus.getLastActiveAt());
    }

}
