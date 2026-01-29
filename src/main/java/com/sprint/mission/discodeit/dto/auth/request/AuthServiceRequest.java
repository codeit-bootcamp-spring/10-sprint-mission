package com.sprint.mission.discodeit.dto.auth.request;

public record AuthServiceRequest(
        String username,
        String password
) {
}
