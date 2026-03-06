package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    //BinaryContentDto create(BinaryContentCreateRequestDTO req);

    BinaryContentDto find(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

}
