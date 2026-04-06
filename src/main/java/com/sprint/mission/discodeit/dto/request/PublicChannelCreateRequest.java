package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름은 필수 입력 사항입니다.")
    @Size(min = 2, max = 50, message = "채널 이름은 2자 이상 50자 이하로 입력해주세요.")
    String name,

    @Size(max = 200, message = "채널 설명은 최대 200자까지 입력 가능합니다.")
    String description
) {

}
