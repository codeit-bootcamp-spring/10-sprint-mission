package com.sprint.mission.discodeit.dto.pagedto;

import java.util.List;

public record PageResponseDto<T>(
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {

}
