package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDTO create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO);
    BinaryContentResponseDTO find(UUID binaryContentId);
    List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
