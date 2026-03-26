package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateDTO(
    @NotNull(message = "채널 이름은 필수입니다.")
    String name,

    @NotNull(message = "채널 설명은 필수입니다.")
    String description
) {

}
