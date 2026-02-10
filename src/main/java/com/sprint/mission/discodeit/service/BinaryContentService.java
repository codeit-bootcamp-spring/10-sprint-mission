package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.Response create(BinaryContentDto.CreateRequest request);
    BinaryContentDto.Response findById(UUID id);
    BinaryContent findEntityById(UUID id);
    List<BinaryContentDto.Response> findAllIdIn(List<UUID> ids);
    void delete(UUID id);
}
