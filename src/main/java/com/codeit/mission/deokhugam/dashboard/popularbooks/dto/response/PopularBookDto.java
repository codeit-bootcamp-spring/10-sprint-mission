package com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response;

import com.codeit.mission.deokhugam.dashboard.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "인기 도서 집계 응답")
public record PopularBookDto(
    @Schema(
        description = "인기 도서 집계 데이터 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID id,

    @Schema(
        description = "대상 도서 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID bookId,

    @Schema(
        description = "도서명",
        example = "string"
    )
    String title,

    @Schema(
        description = "저자",
        example = "string"
    )
    String author,

    @Schema(
        description = "집계 기간",
        allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"},
        example = "DAILY"
    )
    PeriodType period,

    @Schema(
        description = "도서 순위",
        example = "1"
    )
    Long rank,

    @Schema(
        description = "인기도 점수",
        example = "0.1"
    )
    double score,

    @Schema(
        description = "리뷰 개수",
        example = "1"
    )
    Long reviewCount,

    @Schema(
        description = "도서 평점",
        example = "1.0"
    )
    double rating,

    @Schema(
        description = "생성 시점",
        example = "2026-04-24T02:49:51.932Z"
    )
    Instant createdAt
) implements Serializable {

}
