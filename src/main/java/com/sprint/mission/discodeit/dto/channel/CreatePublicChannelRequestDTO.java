package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePublicChannelRequestDTO(
        @NotBlank(message = "공개 채널의 이름은 공백이 될 수 없습니다.")
        @NotNull(message = "공개 채널의 이름은 null일 수 없습니다.")
        String name,
        @NotNull(message = "공개 채널의 설명은 null일 수 없습니다.")
        @NotBlank(message = "공개 채널의 설명은 공백이 될 수 없습니다.")
        String description
) {}
