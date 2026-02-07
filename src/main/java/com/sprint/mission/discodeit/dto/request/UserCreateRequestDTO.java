package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDTO(
    @NotBlank(message = "username이 비어있습니다")
    String username,

    @NotBlank(message = "email이 비어있습니다")
    @Email(message = "email 형식과 맞지 않습니다")
    String email,

    @NotBlank(message = "password가 비어있습니다")
    String password,

    ProfileCreateRequestDTO profileImage
) {}
