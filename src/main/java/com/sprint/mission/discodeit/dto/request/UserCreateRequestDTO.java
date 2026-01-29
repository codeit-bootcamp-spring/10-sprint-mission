package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserCreateRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @Valid
    @NotNull
    private UserStatusCreateRequestDTO userStatusCreateRequestDTO;

    @Valid
    private BinaryContentCreateRequestDTO binaryContentCreateRequestDTO;
}