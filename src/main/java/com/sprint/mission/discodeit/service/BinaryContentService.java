package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(CreateBinaryContentRequestDTO dto);

    BinaryContentDto findById(UUID binaryContentId);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    List<BinaryContentDto> findAll();

    void delete(UUID binaryContentId);
}
