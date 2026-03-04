package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.binaryContentResponse create(BinaryContentDto.binaryContentCreateRequest createReq);
    BinaryContentDto.binaryContentResponse findById(UUID uuid);
    List<BinaryContentDto.binaryContentResponse> findAllByIdIn(List<UUID> uuids);
    void deleteById(UUID uuid);
}
