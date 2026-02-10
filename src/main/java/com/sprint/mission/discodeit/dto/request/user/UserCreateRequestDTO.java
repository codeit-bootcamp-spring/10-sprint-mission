package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCreateRequestDTO (
    @Email
    @NotBlank
    String email,

    @NotBlank
    String password,

    @NotBlank
    String nickname,

    @Valid
    BinaryContentCreateRequestDTO binaryContentCreateRequestDTO
) {

}