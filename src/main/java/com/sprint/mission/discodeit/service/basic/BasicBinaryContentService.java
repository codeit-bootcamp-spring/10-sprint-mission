package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContent create(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new FileEmptyException();
    }

    try {
      BinaryContent binaryContent = new BinaryContent(
          file.getOriginalFilename(),
          file.getSize(),
          file.getContentType()
      );
      binaryContentRepository.save(binaryContent);

      binaryContentStorage.put(binaryContent.getId(), file.getBytes());
      return binaryContent;

    } catch (IOException e) {
      throw new FileUploadException(e);
    }
  }

  @Override
  public BinaryContent findById(UUID id) {
    return getOrThrowBinaryContent(id);
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids);
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    BinaryContent content = getOrThrowBinaryContent(id);
    binaryContentRepository.delete(content);
  }

  // --- Helper Methods ---

  // 바이너리 컨텐츠 검증
  private BinaryContent getOrThrowBinaryContent(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(BinaryContentNotFoundException::new);
  }
}
