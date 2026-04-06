package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "name은 필수입니다.")
    @Size(max = 100, message = "name은 100자 이하여야 합니다.")
    String name,
    @NotBlank(message = "description은 필수입니다.")
    @Size(max = 500, message = "description은 500자 이하여야 합니다.")
    String description
) {

}
