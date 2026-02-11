package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentRequest request);
    BinaryContentResponse find(UUID id);
    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);

    BinaryContent findContent(UUID id); // Entity 직접 반환
}
