package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentDto create(MultipartFile multipartFile);

  BinaryContentDto findById(UUID binaryContentId);

  List<BinaryContentDto> findAllByIdIn(List<UUID> contentsIds);

  BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status,
      Collection<UUID> receiverIds);

  void delete(UUID binaryContentId);
}
