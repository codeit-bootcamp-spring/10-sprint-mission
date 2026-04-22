package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto find(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);
}
