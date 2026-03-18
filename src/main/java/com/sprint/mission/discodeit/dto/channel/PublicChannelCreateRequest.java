package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름을 입력해주세요.")
    String name,

    String description
) {

}
