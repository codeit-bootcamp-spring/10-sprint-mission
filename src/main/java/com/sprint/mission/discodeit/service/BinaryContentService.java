package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDTO.Response create(BinaryContentDTO.Create createRequest);
    BinaryContentDTO.Response findById(UUID binaryContentId);
    List<BinaryContentDTO.Response> findAllByIdIn(List<UUID> contentsIds);
    void delete(UUID binaryContentId);
}
