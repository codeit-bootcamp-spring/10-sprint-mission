package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(CreateBinaryContentRequestDto request);
    BinaryContentDto find(UUID contentId);
    List<BinaryContentDto> findAllByIdIn(List<UUID> contentIds);
    BinaryContent findEntity(UUID contentId); // 심화 요구사항 충족을 위한 메서드
    void delete(UUID contentId);
}
