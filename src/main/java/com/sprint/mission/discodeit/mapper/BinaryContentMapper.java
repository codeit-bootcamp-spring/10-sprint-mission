package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentInfoDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

    public BinaryContentInfoDto toBinaryContentInfoDto(BinaryContent binaryContent) {
        return new BinaryContentInfoDto(binaryContent.getId(), binaryContent.getContent());
    }
}
