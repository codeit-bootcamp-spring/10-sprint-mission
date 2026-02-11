package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreation;
import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreation model) throws IOException;

    BinaryContentResponse find(UUID id) throws IOException, ClassNotFoundException;

    void delete(UUID id) throws IOException;

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
}
