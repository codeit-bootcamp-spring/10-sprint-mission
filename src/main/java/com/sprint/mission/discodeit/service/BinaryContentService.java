package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);
    BinaryContentResponse findById(UUID binaryContentId);
    List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
