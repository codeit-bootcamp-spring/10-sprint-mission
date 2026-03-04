package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentDto create(MultipartFile file);

  BinaryContentDto findById(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

  void deleteById(UUID id);

  // BinaryContentController에서 파일 조회할 때 엔티티를 반환하기 위해 만든 메서드
  BinaryContent findEntity(UUID id);

  List<BinaryContent> findEntities(List<UUID> ids);
}
