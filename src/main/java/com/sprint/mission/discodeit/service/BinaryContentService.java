package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateRequestDto binaryContentCreateRequestDto);
    BinaryContentResponseDto find(UUID binaryContentId);
    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList);
    void delete(UUID binaryContentId);
}
