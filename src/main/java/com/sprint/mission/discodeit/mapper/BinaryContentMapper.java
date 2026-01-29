package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

public class BinaryContentMapper {
    public static BinaryContentResponseDto contentToDto(BinaryContent content) {
        return new BinaryContentResponseDto(
                content.getId(),
                content.getFileName(),
                content.getBytes()
        );
    }
}
