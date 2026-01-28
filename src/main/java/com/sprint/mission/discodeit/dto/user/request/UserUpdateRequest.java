package com.sprint.mission.discodeit.dto.user.request;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userID,
        String name
) { }
