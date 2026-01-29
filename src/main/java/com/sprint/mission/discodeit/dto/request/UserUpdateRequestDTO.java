package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequestDTO {
    @NotNull
    private UUID id;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @Valid
    private UserStatusUpdateRequestDTO userStatusCreateRequestDTO;

    @Valid
    private BinaryContentCreateRequestDTO binaryContentCreateRequestDTO;
}
