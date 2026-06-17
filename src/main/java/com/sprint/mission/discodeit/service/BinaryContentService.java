package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContent create(MultipartFile file);

  BinaryContent findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  BinaryContent updateStatus(UUID binaryContentId, BinaryContentStatus status);

  void deleteById(UUID id);
}
