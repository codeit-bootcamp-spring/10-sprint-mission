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
@Schema(description = "알림 정보 응답")
public class NotificationDto {

  @Schema(description = "알림 ID", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  @Schema(description = "알림 수신자 ID", example = "550e8400-e29b-41d4-a716-446655440001")
  private UUID receiverId;

  @Schema(description = "알림 제목", example = "권한이 변경되었습니다.")
  private String title;

  @Schema(description = "알림 내용", example = "USER -> CHANNEL_MANAGER")
  private String content;

  @Schema(description = "알림 생성 시각", example = "2026-06-02T09:30:00Z")
  private Instant createdAt;
}
