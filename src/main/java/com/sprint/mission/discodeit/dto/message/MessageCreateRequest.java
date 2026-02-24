package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(name = "messageCreateRequest", description = "메시지 생성 요청")
public record MessageCreateRequest(
    @NotBlank
    @Schema(description = "메시지 본문", example = "안녕하세요")
    String content,
    @NotNull
    @Schema(description = "작성자 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID authorId,
    @NotNull
    @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
    UUID channelId
) {

}
