package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.util.Collection;
import java.util.UUID;

public record BinaryContentUpdateEvent(
    BinaryContentDto binaryContentDto,
    Collection<UUID> receiverIds
) {

}
