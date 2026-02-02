package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDTO createProfile(UUID userId, CreateBinaryContentRequestDTO dto);

    BinaryContentResponseDTO createMessageAttachment(UUID userId, UUID messageId, CreateBinaryContentRequestDTO dto);

    BinaryContentResponseDTO findById(UUID binaryContentId);

    List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids);

    void delete(UUID binaryContentId);
}
