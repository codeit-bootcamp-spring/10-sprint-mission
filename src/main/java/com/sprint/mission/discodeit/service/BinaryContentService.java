package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateDto dto);
    BinaryContent findId(UUID id);
    BinaryContent findBinaryContentByUserId(UUID id);
    List<UUID> findAllIdIn();
    List<BinaryContent> findAllByMessageId(UUID messageId);
    void delete(UUID id);
}
