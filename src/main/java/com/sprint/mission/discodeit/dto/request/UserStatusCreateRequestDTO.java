package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequestDTO(
   @NotBlank(message = "UserStatus 생성시 userId가 blank일 수 없습니다")
   UUID userId,
   @NotBlank(message = "UserStatus 생성시 lastAccessTime이 blank일 수 없습니다")
   Instant lastAccessTime
) {}
