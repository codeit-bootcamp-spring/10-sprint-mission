package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    private UserStatusCreateRequestDTO userStatusCreateRequestDTO;

    private BinaryContentCreateRequestDTO binaryContentCreateRequestDTO;
}