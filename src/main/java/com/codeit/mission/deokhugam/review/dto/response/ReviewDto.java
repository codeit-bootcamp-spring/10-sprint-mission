package com.codeit.mission.deokhugam.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

import java.util.UUID;

@Schema(description = "리뷰 응답")
@Builder
public record ReviewDto(
    @Schema(
        description = "리뷰 ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID id,

    @Schema(
        description = "대상 도서 ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID bookId,

    @Schema(
        description = "대상 도서명",
        example = "string"
    )
    String bookTitle,

    @Schema(
        description = "대상 도서 URL 주소",
        example = "http://codeit.com"
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
        description = "내용",
        example = "string"
    )
    String content,

    @Schema(
        description = "평점",
        example = "1"
    )
    int rating,

    @Schema(
        description = "좋아요 수",
        example = "1"
    )
    int likeCount,

    @Schema(
        description = "댓글 수",
        example = "1"
    )
    int commentCount,

    @Schema(
        description = "작성자의 좋아요 여부",
        example = "true"
    )
    Boolean likedByMe,

    @Schema(
        description = "생성 시점",
        example = "2026-04-24T02:49:51.932Z"
    )
    Instant createdAt,

    @Schema(
        description = "수정 시점",
        example = "2026-04-24T02:49:51.932Z"
    )
    Instant updatedAt
) {

}
