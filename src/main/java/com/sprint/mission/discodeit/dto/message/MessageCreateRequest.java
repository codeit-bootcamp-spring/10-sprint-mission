package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Message 생성 정보")
public record MessageCreateRequest (
        @Schema(description = "메시지 본문", example = "hello world")
        @NotBlank(message = "content는 비어 있을 수 없습니다.")
        String content,
        @Schema(description = "메시지를 생성할 채널 ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,
        @Schema(description = "메시지 작성자 ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        @NotNull(message = "authorId는 필수입니다.")
        UUID authorId
) {}
