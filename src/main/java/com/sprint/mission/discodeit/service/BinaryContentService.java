package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    UUID createBinaryContent(UUID ownerId, BinaryContentRequest request);

    BinaryContent findBinaryContent(UUID binaryContentId);

    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

    void deleteBinaryContent(UUID binaryContentId);
}
