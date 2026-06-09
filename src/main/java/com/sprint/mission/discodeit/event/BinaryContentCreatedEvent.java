package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record BinaryContentCreatedEvent(
    BinaryContent binaryContent,
    byte[] bytes
) {

}
