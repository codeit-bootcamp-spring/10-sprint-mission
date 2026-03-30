package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Public 채널 생성 시 필요한 데이터
public record PublicChannelCreateRequest(

    @NotBlank(message = "채널명은 필수입니다.")
    @Size(max = 30, message = "채널명은 30자 이하로 입력해주세요.")
    String name,

    @Size(max = 50, message = "채널 설명은 50자 이하로 입력해주세요.")
    String description
) {

}
