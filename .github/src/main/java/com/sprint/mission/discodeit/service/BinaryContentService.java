package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.Response create(BinaryContentDto.CreateRequest request);
    BinaryContentDto.Response find(UUID userId);
    List<BinaryContentDto.Response> findAllByIdIn(List<UUID> idList);
    void delete(UUID id);
}
