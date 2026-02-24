package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentResponse create(MultipartFile multipartFile);

  BinaryContentResponse findById(UUID binaryContentId);

  List<BinaryContentResponse> findAllByIdIn(List<UUID> contentsIds);

  void delete(UUID binaryContentId);
}
