package com.sprint.mission.discodeit.dto.user.request;

public record AuthServiceRequest(
        String username,
        String password
) {
}
