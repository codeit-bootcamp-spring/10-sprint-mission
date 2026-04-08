package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Schema(name = "MessageCreateRequest", description = "Message 생성 정보")
public record CreateMessageRequestDto(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String content,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId,

        @Schema(hidden = true)
        List<BinaryContentDto> attachments
) {}