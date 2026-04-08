package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageResponse")
public record PageResponse<T>(
        @ArraySchema(schema = @Schema(type = "object", implementation = Object.class))
        List<T> content,
        Object nextCursor,
        int size,
        boolean hasNext,
        Long totalElements
) {
    public PageResponse(List<T> content, Object nextCursor, int size, boolean hasNext){
        this(content, nextCursor, size, hasNext, null);
    }
}
