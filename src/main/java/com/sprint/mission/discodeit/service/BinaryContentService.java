package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.websocket.Decoder;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequestDto binaryContentCreateRequestDto);
    BinaryContent find(UUID binaryContentId);
    List<BinaryContent> findAllByIdIn(List<UUID> idList);
    void delete(UUID binaryContentId);
}
