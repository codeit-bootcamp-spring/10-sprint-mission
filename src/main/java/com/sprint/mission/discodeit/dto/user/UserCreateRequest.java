package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
    @JsonProperty("username")
    String userName,
    String email,
    String password,
    @JsonIgnore
    ProfileImageCreateRequest profileImage
) {

}