package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class UserDto {
    private UserDto() {}

    public record userLoginRequest(String username, String password) {}
    public record userCreateRequest(String username, String password, String email) {}
    public record userUpdateRequest(@JsonProperty("newUsername") String username,
                                    @JsonProperty("newPassword") String password,
                                    @JsonProperty("newEmail") String email) {}
    public record userResponse(@JsonProperty("id") UUID uuid, Instant createdAt, Instant updatedAt,
                               String username, String email,
                               UUID profileId, boolean online) {}
}
