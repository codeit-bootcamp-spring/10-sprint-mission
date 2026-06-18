package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;

public record BinaryContentUpdatedEvent(
    BinaryContentDto binaryContent
) {
}