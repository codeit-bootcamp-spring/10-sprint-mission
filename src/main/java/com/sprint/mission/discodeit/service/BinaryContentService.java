package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentRequestCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentRequestUpdateDto;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentRequestCreateDto request);

    BinaryContentResponseDto find(UUID id);

    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}
