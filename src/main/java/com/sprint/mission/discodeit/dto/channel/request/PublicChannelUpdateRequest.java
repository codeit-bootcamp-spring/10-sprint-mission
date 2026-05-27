package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Size(min = 1, max = 20)
        @Pattern(regexp = "^\\S.*$", message = "channelName은 공백으로 시작할 수 없습니다.")
        String newName,

        @Size(max = 100)
        @Pattern(regexp = "^\\S.*$", message = "channelDescription은 공백으로 시작할 수 없습니다.")
        String newDescription
) {
}
