package com.codeit.mission.deokhugam.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "댓글 응답")
public record CommentDto(
    @Schema(
        description = "댓글 ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID id,

    @Schema(
        description = "대상 리뷰 ID",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID reviewId,

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
        description = "생성 시점",
        example = "2026-04-24T06:02:16.849Z"
    )
    Instant createdAt,

    @Schema(
        description = "수정 시점",
        example = "2026-04-24T06:02:16.849Z"
    )
    Instant updatedAt
) {

}
