package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentResponseDto toBinaryContentInfoDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(binaryContent.getId(), binaryContent.getContentType(), binaryContent.getContent());
    }
}
