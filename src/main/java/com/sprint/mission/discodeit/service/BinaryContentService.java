package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(CreateBinaryContentRequest request);

    BinaryContentResponse find(UUID id);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

}
