package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "수신 정보 응답")
public record ReadStatusDto(
    @Schema(description = "수신 정보 ID", example = "0d56555c-7d86-4fa7-b5d6-3170a70909e1")
    UUID id,
    @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID userId,
    @Schema(description = "채널 ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
    UUID channelId,
    @Schema(description = "마지막 수신 시각", example = "2026-02-23T01:30:54Z")
    Instant lastReadAt,
    @Schema(description = "알림 수신 여부", example = "true")
    Boolean notificationEnabled
) {

}
