package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "수신 정보 생성 요청")
public record ReadStatusCreateRequest(
    @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID userId,
    @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
    UUID channelId
) {

}
