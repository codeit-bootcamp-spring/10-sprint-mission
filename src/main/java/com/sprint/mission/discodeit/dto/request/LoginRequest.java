package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "username은 필수입니다.")
    @Size(max = 50, message = "username은 50자 이하여야 합니다.")
    String username,
    @NotBlank(message = "password는 필수입니다.")
    @Size(max = 60, message = "password는 60자 이하여야 합니다.")
    String password
) {

}
