package com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(description = "커서 기반 페이징 응답")
public record CursorPageResponsePopularBookDto(
    @Schema(
        description = "페이지 내용"
    )
    List<PopularBookDto> content,

    @Schema(
        description = "다음 페이지 커서",
        example = "string"
    )
    String nextCursor,

    @Schema(
        description = "마지막 요소의 생성 시간",
        example = "2025-04-06T15:04:05.000Z"
    )
    String nextAfter,

    @Schema(
        description = "페이지 크기",
        example = "10"
    )
    int size,

    @Schema(
        description = "총 요소 수",
        example = "100"
    )
    long totalElements,

    @Schema(
        description = "다음 페이지 여부",
        example = "true"
    )
    boolean hasNext
) implements Serializable {

}
