package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageUpdateRequestDTO {
    @NotNull
    private UUID id;

    @NotBlank
    private String message;
}
