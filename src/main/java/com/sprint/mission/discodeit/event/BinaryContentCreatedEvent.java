package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
public class BinaryContentCreatedEvent {
    private final UUID receiverId;
    private final UUID binaryContentId;
    private byte[] bytes;

    public BinaryContentCreatedEvent(UUID receiverId,UUID binaryContentId, byte[] bytes){
        this.receiverId = receiverId;
        this.binaryContentId = binaryContentId;
        this.bytes = bytes;
    }

    public void clear(){
        this.bytes = null;
    }
}
