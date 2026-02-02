package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto dto);
    BinaryContentResponseDto findId(UUID id);
    BinaryContentResponseDto findBinaryContentByUserId(UUID id);
    List<BinaryContentResponseDto> findAllBId(UUID id);
    List<BinaryContentResponseDto> findAllByMessageId(UUID messageId);
    void delete(UUID id);
}
