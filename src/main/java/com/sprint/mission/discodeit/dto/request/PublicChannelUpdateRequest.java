package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Pattern(regexp = "^(?!\\s*$).+", message = "newName은 공백일 수 없습니다.")
    @Size(max = 100, message = "newName은 100자 이하여야 합니다.")
    String newName,
    @Pattern(regexp = "^(?!\\s*$).+", message = "newDescription은 공백일 수 없습니다.")
    @Size(max = 500, message = "newDescription은 500자 이하여야 합니다.")
    String newDescription
) {

}
