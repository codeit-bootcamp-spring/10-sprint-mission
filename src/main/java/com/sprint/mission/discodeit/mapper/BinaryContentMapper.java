package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public class BinaryContentMapper {
    public static BinaryContent toEntity(UUID userId, BinaryContentCreateDTO dto) {
        return new BinaryContent(
                userId,
                dto.data(),
                dto.contentType(),
                dto.filename()
        );
    }
}
