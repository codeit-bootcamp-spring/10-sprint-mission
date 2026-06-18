package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    BinaryContent binaryContent,
    byte[] bytes,
    UUID targetChannelId
) {

}
