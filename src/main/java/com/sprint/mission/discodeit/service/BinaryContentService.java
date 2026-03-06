package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCreateRequest binaryContentCreateRequest);
    BinaryContentDto find(UUID binaryContentId);
    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
