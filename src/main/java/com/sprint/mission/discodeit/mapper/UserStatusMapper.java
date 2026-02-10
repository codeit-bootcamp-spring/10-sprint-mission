package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {
    public UserStatusResponseDto toDto(UserStatus userStatus){
        if(userStatus == null) return null;

        return new UserStatusResponseDto(userStatus.getId(),
                                        userStatus.getUserId(),
                                        userStatus.getLastOnlineAt());
    }
}
