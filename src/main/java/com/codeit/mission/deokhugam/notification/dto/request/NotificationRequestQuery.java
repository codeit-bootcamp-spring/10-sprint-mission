package com.codeit.mission.deokhugam.notification.dto.request;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import lombok.Builder;

@Schema(description = "목록 조회할 알림 정보")
@Builder
public record NotificationRequestQuery(
  @Schema(
    description = "정렬 방향",
    allowableValues = {"ASC", "DESC"},
    defaultValue = "DESC",
    example = "DESC"
  )
  DirectionEnum direction, // note: 공통 enum으로 빼도 될듯

  @Schema(
    description = "커서 페이지네이션 커서",
    example = "2025-04-06T15:04:05.000Z"
  )
  Instant cursor,

  @Schema(
    description = "보조 커서 (createdAt)",
    example = "2025-04-06T15:04:05.000Z"
  )
  Instant after,

  @Schema(
    description = "페이지 크기",
    defaultValue = "20",
    minimum = "1",
    maximum = "100",
    example = "20"
  )
  @Min(value = 1, message = "Page size must not be less than one")
  @Max(value = 100, message = "Page size must be no more than 100")
  Integer limit

) {

  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 100;

  // 기본값 설정을 위한 생성자
  public NotificationRequestQuery {
    if (direction == null) {
      direction = DirectionEnum.DESC;
    }
    if (limit == null) {
      limit = DEFAULT_LIMIT;
    }
    limit = Math.min(limit, MAX_LIMIT);
  }
}
