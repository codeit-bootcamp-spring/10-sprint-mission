package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateChannelRequestDTO(
        @NotNull(message = "채널 이름은 null일 수 없습니다.")
        @NotBlank(message = "채널 이름은 공백이 될 수 없습니다.")
        String newName,
        @NotNull(message = "채널 설명은 null일 수 없습니다.")
        @NotBlank(message = "채널 설명은 공백이 될 수 없습니다.")
        String newDescription
) { }
