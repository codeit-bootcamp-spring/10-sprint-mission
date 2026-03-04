package com.sprint.mission.discodeit.dto.pagedto;

import java.util.List;

public record PageResponseDto<T>(
    List<T> content,
    Integer number,
    Integer size,
    Boolean hasNext,
    Long totalElements
) {

}
