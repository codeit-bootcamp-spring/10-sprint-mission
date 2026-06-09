package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

public record BinaryContentCreatedEvent (

        BinaryContent binaryContent,
        CreateBinaryContentPayloadDTO payload
) {
}
