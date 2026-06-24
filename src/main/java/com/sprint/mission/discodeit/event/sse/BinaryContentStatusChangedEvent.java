package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;

public record BinaryContentStatusChangedEvent(
    BinaryContentDto binaryContent
) {

}
