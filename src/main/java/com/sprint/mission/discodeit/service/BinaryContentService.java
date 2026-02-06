package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreation;
import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreation model);

    BinaryContentResponse find(UUID id);

    void delete(UUID id);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
}
