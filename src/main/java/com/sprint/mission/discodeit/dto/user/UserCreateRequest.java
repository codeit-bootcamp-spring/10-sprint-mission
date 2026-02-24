package com.sprint.mission.discodeit.dto.user;


import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreateRequest(
    @JsonProperty("username")
    String userName,
    String email,
    String password,
    ProfileImageCreateRequest profileImage
) {

}
