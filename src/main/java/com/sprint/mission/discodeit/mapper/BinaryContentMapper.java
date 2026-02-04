package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BinaryContentMapper {

    public BinaryContentResponse toResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getContentTYpe(),
        binaryContent.getBytes()
        );
    }
}
