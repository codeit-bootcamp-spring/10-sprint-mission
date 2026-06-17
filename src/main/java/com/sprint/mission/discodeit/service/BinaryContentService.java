package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentDto create(MultipartFile attachment) throws IOException;

  BinaryContentDto findById(UUID uuid);

  List<BinaryContentDto> findAllByIdIn(List<UUID> uuids);

  void deleteById(UUID uuid) throws IOException;

  BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status);
}
