package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    byte[] bytes = request.bytes();
    if(bytes == null) throw new IllegalArgumentException("Bytes must not be null");

    // bytes 뺀 메타만 저장.
    BinaryContent meta = new BinaryContent(
            request.fileName(),
            (long) bytes.length,
            request.contentType()
    );
    BinaryContent saved = binaryContentRepository.save(meta);

    // 실제 bytes는 storage 에 저장.
    binaryContentStorage.put(saved.getId(), bytes);

    return saved;
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
