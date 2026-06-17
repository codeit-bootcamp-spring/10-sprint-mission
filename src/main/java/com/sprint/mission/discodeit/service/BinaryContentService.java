package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    // create
    BinaryContentDto create(BinaryContentCreateRequest binaryContentCreateRequest);

    // find
    BinaryContentDto find(UUID binaryContentId);

    // all find
    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

    // update
    BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status);

    // delete
    void delete(UUID binaryContentId);
}
