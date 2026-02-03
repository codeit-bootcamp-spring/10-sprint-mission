package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDTO create(BinaryContentCreateDTO binaryContentCreateDTO);

    BinaryContentDTO find(UUID binaryContentId);

    List<BinaryContentDTO> findAllByIdIn(List<UUID> binaryContentIdList);

    void delete(UUID binaryContentId);
}
