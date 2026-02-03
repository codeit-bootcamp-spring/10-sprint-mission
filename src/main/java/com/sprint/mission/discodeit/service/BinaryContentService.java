package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto dto);
    BinaryContentResponseDto findById(UUID id);
    void delete(UUID id);
}