package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateMessageRequestDto {
    @NotBlank(message = "메시지를 입력하세요.")
    private String content;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;

    @NotNull(message = "작성자 ID는 필수입니다.")
    private UUID authorId;

    private List<BinaryContentDto> attachments;
}
