package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentDto create(MultipartFile file);

  BinaryContentDto find(UUID id);

  void delete(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);
}
