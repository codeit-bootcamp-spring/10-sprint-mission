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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    String contentType = request.contentType();
    byte[] bytes = request.bytes();

    log.info("파일 업로드 요청 - fileName={}, contentType={}", fileName, contentType);

    if (bytes == null) {
      log.warn("파일 업로드 실패 - bytes가 null임 fileName={}", fileName);
      throw new IllegalArgumentException("Bytes must not be null");
    }

    BinaryContent meta = new BinaryContent(
            fileName,
            (long) bytes.length,
            contentType
    );

    BinaryContent saved = binaryContentRepository.save(meta);
    binaryContentStorage.put(saved.getId(), bytes);

    log.info("파일 업로드 완료 - binaryContentId={}, size={}", saved.getId(), bytes.length);

    return saved;
  }

  @Transactional(readOnly = true)
  @Override
  public BinaryContent find(UUID binaryContentId) {
    log.debug("파일 조회 요청 - binaryContentId={}", binaryContentId);

    return binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> {
              log.warn("파일 조회 실패 - 존재하지 않음 binaryContentId={}", binaryContentId);
              return new NoSuchElementException(
                      "BinaryContent with id " + binaryContentId + " not found");
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("파일 다건 조회 요청 - count={}", binaryContentIds.size());
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
            .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    log.info("파일 삭제 요청 - binaryContentId={}", binaryContentId);

    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> {
              log.warn("파일 삭제 실패 - 존재하지 않음 binaryContentId={}", binaryContentId);
              return new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
            });

    binaryContentRepository.delete(binaryContent);

    log.info("파일 삭제 완료 - binaryContentId={}", binaryContentId);
  }
}
