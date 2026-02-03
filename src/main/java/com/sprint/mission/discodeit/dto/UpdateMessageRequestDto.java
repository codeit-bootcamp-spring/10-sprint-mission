package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMessageRequestDto {
    @NotBlank(message = "수정할 메시지 내용을 입력해주세요.")
    private String content;
}
