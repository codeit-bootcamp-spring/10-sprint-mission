package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCreateRequest dto);
    BinaryContentDto findBinaryContent(UUID id);
    List<BinaryContentDto> findAllIdIn(List<UUID> binaryContentIds);
    BinaryContentDto updateStatus(UUID receiverId,UUID binaryContentId, BinaryContentStatus status);
    void delete(UUID id);
}
