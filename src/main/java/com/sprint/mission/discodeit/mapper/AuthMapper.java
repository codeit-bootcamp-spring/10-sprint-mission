package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.auth.LoginResponseDTO;
import com.sprint.mission.discodeit.entity.User;

public class AuthMapper {
    public static LoginResponseDTO toResponse(User user) {
        return new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
