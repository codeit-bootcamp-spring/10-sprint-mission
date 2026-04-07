package com.sprint.mission.discodeit.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChannelCreatePublicRequest(
    @NotBlank(message = "채널 이름은 비어 있을 수 없습니다.")
    @Size(max = 20, message = "채널 이름은 최대 20자 까지입니다.")
    String name,
    @Size(max = 50, message = "채널 설명은 최대 50자 까지입니다.")
    String description
) {

}
