package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(

    @NotBlank(message = "PUBLIC 채널의 이름은 필수 입니다.")
    String name,

    @Size(max = 255, message = "설명은 255자 이하 입니다.")
    String description
) {

}
