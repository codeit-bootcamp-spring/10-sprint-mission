package com.sprint.mission.discodeit.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 정보")
public record LoginRequest(
    @JsonProperty("username")
    String userName,
    String password
) {

}