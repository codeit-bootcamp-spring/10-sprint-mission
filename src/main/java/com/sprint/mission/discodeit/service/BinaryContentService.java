package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);

  BinaryContentDto findById(UUID id);

  BinaryContent findEntityById(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

  BinaryContentDto updateStatus(UUID id, BinaryContentStatus status);

  void delete(UUID id);
}
