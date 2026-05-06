package com.codeit.mission.deokhugam.dashboard.powerusers.dto.response;

import com.codeit.mission.deokhugam.dashboard.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "파워 유저 집계 응답")
public record PowerUserDto(
    @Schema(
        description = "사용자 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID userId,

    @Schema(
        description = "사용자 닉네임",
        example = "string"
    )
    String nickname,

    @Schema(
        description = "집계 기간",
        allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"},
        example = "DAILY"
    )
    PeriodType period,

    @Schema(
        description = "생성 시점",
        example = "2026-04-24T02:49:51.932Z"
    )
    Instant createdAt,

    @Schema(
        description = "사용자 순위",
        example = "1"
    )
    long rank,

    @Schema(
        description = "인기도 점수",
        example = "1.0"
    )
    double score,

    @Schema(
        description = "리뷰 인기도 점수 합계",
        example = "1.0"
    )
    double reviewScoreSum,

    @Schema(
        description = "좋아요 개수",
        example = "1"
    )
    long likeCount,

    @Schema(
        description = "댓글 개수",
        example = "1"
    )
    long commentCount
) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

}
