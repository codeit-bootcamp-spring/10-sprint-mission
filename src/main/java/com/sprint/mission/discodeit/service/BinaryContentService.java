package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.BinaryContentResponse create(BinaryContentDto.BinaryContentRequest request);
    BinaryContentDto.BinaryContentResponse findById(UUID binaryContentId);
    List<BinaryContentDto.BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
