package com.sprint.mission.discodeit.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
    @JsonProperty("username")
    String userName,
    String password
) {

}
