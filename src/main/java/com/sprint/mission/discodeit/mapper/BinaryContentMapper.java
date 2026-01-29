package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

public class BinaryContentMapper {
    public static BinaryContentResponseDto toDto(BinaryContent content) {
        return new BinaryContentResponseDto(
                content.getId(),
                content.getFileName(),
                content.getBytes()
        );
    }
    public static BinaryContent toEntity(BinaryContentCreateDto dto) {
        return new BinaryContent(dto.fileName(), dto.bytes());
    }
}
