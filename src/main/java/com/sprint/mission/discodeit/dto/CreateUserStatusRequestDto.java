package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateUserStatusRequestDto {
    @NotNull(message = "유저 ID는 필수입니다.")
    private UUID userId;
}
