package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MessageCreateRequest {
    @NotNull(message = "작성자 ID는 필수입니다.")
    private UUID authorId;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;

    @NotBlank(message = "메시지 내용은 빈 칸일 수 없습니다.")
    private String content;
}
