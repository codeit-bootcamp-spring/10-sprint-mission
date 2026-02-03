package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.response create(BinaryContentDto.createRequest createReq);
    BinaryContentDto.response findById(UUID uuid);
    List<BinaryContentDto.response> findAllByIdIn(List<UUID> uuids);
    void deleteById(UUID uuid);
}
