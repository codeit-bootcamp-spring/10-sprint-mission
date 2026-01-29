package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.websocket.Decoder;

import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequestDto binaryContentCreateRequestDto);
    BinaryContent find(UUID binaryContentId);
    BinaryContent findAllByIdln();
    void delete(UUID binaryContentId);
}
