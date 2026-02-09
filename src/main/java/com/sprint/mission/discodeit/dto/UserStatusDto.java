package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    public record CreateRequest(
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID userid,
            Instant lastActiveAt
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            Instant lastActiveAt
    ) {}

    public record UpdateRequest(
            @NotNull(message = "마지막 활동 시간은 필수입니다.")
            Instant newLastActiveAt
    ){}
}
