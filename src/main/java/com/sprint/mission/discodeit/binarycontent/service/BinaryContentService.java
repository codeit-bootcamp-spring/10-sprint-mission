package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);
    BinaryContentResponse find(UUID binaryContentId);
    List<BinaryContentResponse> findAllByIdIn (List<UUID> ids);
    void delete(UUID id);
}
