package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record UserStatusUpdateRequestDTO(
        @NotBlank(message = "수정하려는 UserStatus의 lastAccessTime이 blank일 수 없습니다")
        Instant lastAccessTime
) {}
