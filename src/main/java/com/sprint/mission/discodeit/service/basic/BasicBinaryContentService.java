package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    byte[] bytes = request.bytes();
    if(bytes == null) throw new IllegalArgumentException("Bytes must not be null");
    BinaryContent binaryContent = new BinaryContent(
            request.fileName(),
            (long) bytes.length,
            request.contentType(),
            bytes
    );
    return binaryContentRepository.save(binaryContent);
  }

  @Transactional(readOnly = true)
  @Override
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() ->
                    new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

    binaryContentRepository.delete(binaryContent);
  }
}
