package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class MessageCreateRequest {
    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;

    @NotNull(message = "작성자 ID는 필수입니다.")
    private UUID authorId;

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(max = 2000, message = "메시지 내용은 2000자를 초과할 수 없습니다.")
    private String content;

    public MessageCreateRequest(UUID channelId, UUID authorId, String content) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }
}
