package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageUpdateRequest {
    @NotBlank(message = "메시지 내용은 빈 칸일 수 없습니다.")
    private String newContent;
}
