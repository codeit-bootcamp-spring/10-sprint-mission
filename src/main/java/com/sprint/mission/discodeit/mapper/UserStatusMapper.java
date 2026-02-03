package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {
    public UserStatusResponse toResponse(UserStatus us){
        return new UserStatusResponse(
                us.getId(),
                us.getUserId(),
                us.getLastLoginAt(),
                us.isOnline()
        );
    }
}
