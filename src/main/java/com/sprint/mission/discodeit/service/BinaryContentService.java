package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentDto.Response create(MultipartFile multipartFile);

  BinaryContentDto.Response findById(UUID binaryContentId);

  List<BinaryContentDto.Response> findAllByIdIn(List<UUID> contentsIds);

  void delete(UUID binaryContentId);
}
