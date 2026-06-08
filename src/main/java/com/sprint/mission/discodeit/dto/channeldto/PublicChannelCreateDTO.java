package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateDTO(

    @NotBlank(message = "채널 Null이거나 공백일 수 없습니다.")
    String name,
    @NotNull(message = "채널 설명은 Null이 될 수 없습니다.")
    String description
) {

}
