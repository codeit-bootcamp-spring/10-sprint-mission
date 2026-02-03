package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    public record CreateRequest(
            @NotBlank
            UUID userid,
            Instant lastActiveAt
    ) {}

    public record UpdateRequest(
            Instant newLastActiveAt
    ){}
}
