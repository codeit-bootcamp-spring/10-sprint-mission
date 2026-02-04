package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentInfoDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentInfoDto create(BinaryContentCreateDto binaryContentCreateDto);

    BinaryContentInfoDto find(UUID id);

    List<BinaryContentInfoDto> findAllByIdIn(List<UUID> idList);

    void delete(UUID id);

}
