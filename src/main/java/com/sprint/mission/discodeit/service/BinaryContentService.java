package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(CreateBinaryContentRequestDto request);
    BinaryContentDto find(UUID contentId);
    List<BinaryContentDto> findAllByIdIn(List<UUID> contentIds);
    void delete(UUID contentId);
}
