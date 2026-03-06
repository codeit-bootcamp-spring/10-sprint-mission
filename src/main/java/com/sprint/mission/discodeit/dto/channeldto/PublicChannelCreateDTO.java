package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateDTO(
    @NotBlank(message = "채널 이름은 필수입니다.")
    @NotNull
    String name,

    @NotBlank(message = "채널 설명은 필수입니다.")
    @NotNull
    String description
) {

}
