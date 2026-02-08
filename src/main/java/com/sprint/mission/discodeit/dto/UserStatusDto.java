package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusDto {
    public record CreateRequest(
            @NotBlank
            UUID userid,
            Instant lastActiveAt) {
    }

    public record UpdateRequest(
            Instant lastActiveAt
    ) {}
}
