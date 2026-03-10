package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
    @JsonIgnore
    UUID userId,

    @JsonProperty("newUsername")
    Optional<String> userName,

    @JsonProperty("newEmail")
    Optional<String> email,

    @JsonProperty("newPassword")
    Optional<String> password,

    @JsonIgnore
    Optional<ProfileImageCreateRequest> profileImage
) {

}