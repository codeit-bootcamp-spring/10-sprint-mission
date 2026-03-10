package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import java.util.List;

public record PageResponse(
    List<MessageDto> content,
    int number,
    int size,
    boolean hasNext,
    Long totalElements
) {

}