package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 50, message = "name은 50자 이하여야 합니다.")
        String username,
        @NotBlank(message = "password는 필수입니다.")
        @Size(min=4, message="password는 4자 이상이어야 합니다.")
        String password
) {

}
