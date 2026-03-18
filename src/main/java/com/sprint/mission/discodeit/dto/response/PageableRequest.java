package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "Pageable", description = "페이징 정보")
public record PageableRequest(
        @Schema(description = "페이지 번호", minimum = "0", example = "0")
        Integer page,
        @Schema(description = "페이지 크기", minimum = "1", example = "50")
        Integer size,
        @ArraySchema(schema = @Schema(description = "정렬 조건", example = "createdAt,desc"))
        List<String> sort
) {
}
