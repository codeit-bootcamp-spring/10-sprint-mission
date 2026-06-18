package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.util.Collection;
import java.util.UUID;

public record KafkaBinaryContentUpdateEvent(
    BinaryContentDto binaryContentDto,
    Collection<UUID> receiverIds
) {

}
