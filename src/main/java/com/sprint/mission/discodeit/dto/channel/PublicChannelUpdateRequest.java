package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
        @NotBlank(message = "채널 이름을 입력해주세요.")
        String newName,

        @NotBlank(message = "채널 설명을 입력해주세요.")
        String newDescription
) {
}
