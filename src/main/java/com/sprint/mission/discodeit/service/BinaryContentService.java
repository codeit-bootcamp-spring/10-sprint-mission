package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequestDTO req);

  BinaryContentDto find(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);

  BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status);
}
