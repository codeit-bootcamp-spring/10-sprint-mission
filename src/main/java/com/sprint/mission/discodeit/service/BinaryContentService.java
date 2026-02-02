package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.response create(BinaryContentDto.createRequest dto);
    BinaryContentDto.response findById(UUID uuid);
    List<BinaryContentDto.response> findByAllByIdIn(List<UUID> uuids);
    void deleteById(UUID uuid);
}
