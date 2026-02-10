package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank
        String username,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        BinaryContentResponse profileImage
) {
}

