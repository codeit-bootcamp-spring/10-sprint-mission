package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  public BinaryContentDto create(BinaryContentRequest request) {
    log.debug("Creating binary content: {}", request.getFileName());
    BinaryContent binaryContent = new BinaryContent(
        request.getFileName(),
        request.getContentType(),
        request.getContent() != null ? request.getContent().length : 0
    );
    binaryContentRepository.save(binaryContent);

    if (request.getContent() != null) {
      binaryContentStorage.put(binaryContent.getId(), request.getContent());
    }

    log.info("Binary content created successfully: id={}, filename={}", binaryContent.getId(), request.getFileName());
    return binaryContentMapper.toDto(binaryContent);
  }

  public BinaryContentDto find(UUID id) {
    log.debug("Fetching binary content details: id={}", id);
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Binary content not found: id={}", id);
          return new BinaryContentNotFoundException(id);
        });
    return binaryContentMapper.toDto(binaryContent);
  }

  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    log.debug("Fetching multiple binary contents: count={}", ids.size());
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(UUID id) {
    log.debug("Binary content deletion requested: id={}", id);
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Deletion failed - Binary content not found: id={}", id);
          return new BinaryContentNotFoundException(id);
        });
    binaryContentRepository.delete(binaryContent);
    log.info("Binary content deleted successfully: id={}", id);
  }
}
