package com.sprint.mission.discodeit.events;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;

public record BinaryContentUpdatedEvent(
    BinaryContentDto binaryContent
) {

}
