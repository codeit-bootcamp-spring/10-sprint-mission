package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.UserStatusType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatusCreateRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private UserStatusType userStatusType;
}
