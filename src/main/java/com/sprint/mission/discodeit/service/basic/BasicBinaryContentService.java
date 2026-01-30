package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.List;
import java.util.UUID;

public class BasicBinaryContentService implements BinaryContentService {
    @Override
    public BinaryContentResponseDTO create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO) {
        return null;
    }

    @Override
    public BinaryContentResponseDTO findById(UUID id) {
        return null;
    }

    @Override
    public List<BinaryContentResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public List<BinaryContentResponseDTO> findByIdIn(UUID id) {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }
}
