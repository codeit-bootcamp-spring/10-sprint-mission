package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(max = 50)
    String username,

    @NotBlank
    @Size(max = 60)
    String password
) {

}
