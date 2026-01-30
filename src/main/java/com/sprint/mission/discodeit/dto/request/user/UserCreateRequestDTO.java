package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreateRequestDTO {
    @NotNull
    private UUID userId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @Valid
    private BinaryContentCreateRequestDTO binaryContentCreateRequestDTO;
}