package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

@Getter
public class BinaryContentCreatedEvent {
    private final BinaryContent binaryContent;
    private final byte[] bytes;

    public BinaryContentCreatedEvent(BinaryContent binaryContent, byte[] bytes) {
        this.binaryContent = binaryContent;
        this.bytes = bytes;
    }
}
