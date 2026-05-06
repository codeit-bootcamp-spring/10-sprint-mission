package com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response;

import com.codeit.mission.deokhugam.dashboard.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "인기 리뷰 집계 응답")
public record PopularReviewDto(
    @Schema(
        description = "인기 리뷰 집계 데이터 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID id,

    @Schema(
        description = "리뷰 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID reviewId,

    @Schema(
        description = "대상 도서 ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    UUID bookId,

    @Schema(
        description = "도서명",
        example = "string"
    )
    String bookTitle,

    @Schema(
        description = "대상 도서 썸네일 URL",
        example = "http://codeit.com/books/book.png"
    )
    String bookThumbnailUrl,

    @Schema(
        description = "작성자 ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID userId,

    @Schema(
        description = "작성자 닉네임",
        example = "string"
    )
    String userNickname,

    @Schema(
        description = "리뷰 내용",
        example = "string"
    )
    String reviewContent,

    @Schema(
        description = "리뷰 평점",
        example = "1.0"
    )
    double reviewRating,

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
        description = "리뷰 순위",
        example = "1"
    )
    long rank,

    @Schema(
        description = "인기도 점수",
        example = "0.1"
    )
    double score,

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

}
