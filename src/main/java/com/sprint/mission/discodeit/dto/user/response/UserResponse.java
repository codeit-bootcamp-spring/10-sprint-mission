package com.sprint.mission.discodeit.dto.user.response;

import java.util.UUID;

public record UserResponse(
        UUID userID,
        String name,
        UserStatusResponse userStatus // DTO로 수정해야함
) {
}
