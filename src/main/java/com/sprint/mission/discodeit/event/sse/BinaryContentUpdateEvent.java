package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;

public record BinaryContentUpdateEvent(
    BinaryContentDto binaryContentDto
) {

}
