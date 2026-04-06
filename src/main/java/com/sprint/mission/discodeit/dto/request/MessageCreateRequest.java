package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메시지 내용을 입력해주세요.")
    @Size(max = 2000, message = "메시지는 최대 2000자까지 입력 가능합니다.")
    String content,

    @NotNull(message = "채널 ID는 필수 입력 사항입니다.")
    UUID channelId,

    @NotNull(message = "작성자 ID는 필수 입력 사항입니다.")
    UUID authorId
) {

}
