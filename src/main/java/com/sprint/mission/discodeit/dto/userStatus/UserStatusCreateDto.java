package com.sprint.mission.discodeit.dto.userStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatusCreateDto {
    @NotNull(message = "유저 ID는 필수입니다")
    private UUID userId;
}
