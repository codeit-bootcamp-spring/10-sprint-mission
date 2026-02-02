package com.sprint.mission.discodeit.mapper.auth;

import com.sprint.mission.discodeit.dto.auth.response.AuthServiceResponse;
import com.sprint.mission.discodeit.dto.user.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public AuthServiceResponse toResponse(User user, UserStatus status) {
        return new AuthServiceResponse(user.getId(), user.getName(), new UserStatusResponse(status.isOnline()));
    }
}
