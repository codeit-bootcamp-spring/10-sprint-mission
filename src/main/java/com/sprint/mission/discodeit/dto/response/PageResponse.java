package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "페이지네이션 응답 DTO")
public record PageResponse<T>(
        @Schema(description = "실제 데이터")
        List<T> content,
        @Schema(description = "다음 페이지 커서", example = "2026-03-10T10:15:30Z")
        Object nextCursor,
        @Schema(description = "요청 페이지 크기", example = "50")
        int size,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,
        @Schema(description = "전체 데이터 수 (null 가능)", nullable = true, example = "null")
        Long totalElements
) {
}
