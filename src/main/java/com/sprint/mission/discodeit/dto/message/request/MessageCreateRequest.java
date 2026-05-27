package com.sprint.mission.discodeit.dto.message.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "메시지 생성 요청")
public record MessageCreateRequest(

        @Schema(description = "메시지 내용", example = "안녕하세요! 오늘 프로젝트 회의 몇 시인가요?")
        @NotBlank(message = "메시지 내용은 비어있을 수 없습니다.")
        String content,

        @Schema(description = "메시지가 작성될 채널 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "채널 Id는 필수입니다.")
        UUID channelId,

        @Schema(description = "메시지 작성자 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        @NotNull(message = "작성자 Id는 필수입니다.")
        UUID authorId
) {
}