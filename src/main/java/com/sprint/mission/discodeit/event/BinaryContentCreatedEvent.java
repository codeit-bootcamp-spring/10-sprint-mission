package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
public class BinaryContentCreatedEvent {
    private final UUID binaryContentId;
    private byte[] bytes;
    private final UUID receiverId;

    public BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes, UUID receiverId){
        this.binaryContentId = binaryContentId;
        this.bytes = bytes;
        this.receiverId = receiverId;
    }

    public void clear(){
        this.bytes = null;
    }
}
