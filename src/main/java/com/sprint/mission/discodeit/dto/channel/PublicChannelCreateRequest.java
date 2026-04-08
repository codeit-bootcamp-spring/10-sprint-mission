package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
        @NotBlank(message = "채널 이름을 입력해주세요.")
        @Size(min = 1, max = 100, message = "채널 이름은 1자 이상 100자 이하여야 합니다.")
        String name,

        @Size(max = 200, message = "채널 설명은 200자 이하여야 합니다.")
        String description
) {

}
