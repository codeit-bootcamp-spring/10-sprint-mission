package com.sprint.mission.discodeit.auth.jwt.dto;

import com.sprint.mission.discodeit.auth.enums.Role;

public record AccessTokenClaims(
    Role role
) {

}
