package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto binaryContentCreateDto);

    BinaryContentResponseDto findById(UUID id);

    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList);

    void delete(UUID id);

}
