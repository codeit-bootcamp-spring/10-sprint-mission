package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    // create
    BinaryContent createBinaryContent(BinaryContentCreateRequest request);

    // find
    BinaryContent findBinaryContentById(UUID binaryContentId);

    // all find
    List<BinaryContent> findAllBinaryContentByIdIn(List<UUID> binaryContentIds);

    // delete
    void deleteBinaryContent(UUID binaryContentId);
}
