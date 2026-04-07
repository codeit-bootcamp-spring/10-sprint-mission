package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(

    @NotBlank(message = "이름 수정은 필수 입니다.")
    String newName,

    @Size(max = 255, message = "설명은 255자 이하입니다.")
    String newDescription
) {

}
