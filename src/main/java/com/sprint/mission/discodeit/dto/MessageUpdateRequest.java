package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageUpdateRequest {
    @NotBlank(message = "메시지 내용은 공백일 수 없습니다.")
    @Size(max = 2000, message = "메시지 내용은 2000자를 초과할 수 없습니다.")
    private String newContent;
}
