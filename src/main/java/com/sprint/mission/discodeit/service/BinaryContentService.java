package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.Response create(BinaryContentDto.CreateRequest request);
    BinaryContentDto.Response find(UUID binaryContentId);
    List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
}
