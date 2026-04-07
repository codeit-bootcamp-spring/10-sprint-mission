package com.sprint.mission.discodeit.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 정보")
public record LoginRequest(

    @NotBlank(message = "username은 필수입니다.")
    @JsonProperty("username")
    String userName,

    @NotBlank(message = "password는 필수입니다.")
    String password
) {

}
