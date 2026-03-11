package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "PageResponse")
public record PageResponse(
    List<Object> content,
    int number,
    int size,
    boolean hasNext,
    Long totalElements
) {

}