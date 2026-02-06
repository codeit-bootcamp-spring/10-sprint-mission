package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    void create(BinaryContentCreateRequest request);
    BinaryContent find(UUID binaryContentId);
    List<BinaryContent> findAllByIdIn (List<UUID> ids);
    void delete(UUID id);
}
