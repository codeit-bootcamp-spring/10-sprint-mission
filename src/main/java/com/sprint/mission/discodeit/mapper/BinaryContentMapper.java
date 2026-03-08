package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
            binaryContent.getId(),
            binaryContent.getFileName(),
            binaryContent.getSize(),
            binaryContent.getContentType(),
            binaryContent.getBytes()
        );
    }
}
