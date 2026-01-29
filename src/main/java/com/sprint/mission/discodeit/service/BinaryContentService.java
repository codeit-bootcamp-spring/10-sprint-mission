package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto createDto);
    BinaryContentResponseDto find(UUID id);
    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);

}
