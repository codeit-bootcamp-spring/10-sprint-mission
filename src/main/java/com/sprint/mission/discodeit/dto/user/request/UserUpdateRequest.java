package com.sprint.mission.discodeit.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(@JsonProperty("newUsername") String username,
                                @JsonProperty("newEmail") @Email String email,
                                @JsonProperty("newPassword") String password) {
}
