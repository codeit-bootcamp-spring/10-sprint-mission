package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    UUID createBinaryContent(UUID ownerId, BinaryContentRequest request);

    BinaryContentResponse findBinaryContent(UUID binaryContentId);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);

    void deleteBinaryContent(UUID binaryContentId);
}
