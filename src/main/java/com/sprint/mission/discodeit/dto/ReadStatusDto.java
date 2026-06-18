package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "읽음 상태 정보 응답")
public class ReadStatusDto {

  @Schema(description = "읽음 상태 ID", example = "r1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
  private UUID id;

  @Schema(description = "사용자 ID")
  private UUID userId;

  @Schema(description = "채널 ID")
  private UUID channelId;

  @Schema(description = "마지막으로 읽은 시각", example = "2026-02-20T08:40:00Z")
  private Instant lastReadAt;

  @Schema(description = "채널 알림 활성화 여부", example = "true")
  private boolean notificationEnabled;
}
