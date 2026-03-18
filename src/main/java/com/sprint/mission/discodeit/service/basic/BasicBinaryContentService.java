package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public UUID create(BinaryContentCreateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.fileName(), "fileName");
    requireNonNull(request.contentType(), "contentType");
    requireNonNull(request.bytes(), "bytes");

    BinaryContent entity = new BinaryContent(
        request.fileName(),
        request.bytes().length,
        request.contentType()
    );

    BinaryContent saved = binaryContentRepository.save(entity);
    binaryContentStorage.put(saved.getId(), request.bytes());

    return saved.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentResponse find(UUID id) {
    requireNonNull(id, "id");
    BinaryContent found = findEntityOrThrow(id);
    return binaryContentMapper.toResponse(found);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
    requireNonNull(ids, "ids");

    return binaryContentRepository.findAllByIdIn(ids).stream()
        .map(binaryContentMapper::toResponse)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    requireNonNull(id, "id");

    findEntityOrThrow(id);
    binaryContentStorage.delete(id);
    binaryContentRepository.delete(id);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent findEntity(UUID id) {
    requireNonNull(id, "id");
    return findEntityOrThrow(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllEntitiesByIdIn(List<UUID> ids) {
    requireNonNull(ids, "ids");
    return binaryContentRepository.findAllByIdIn(ids);
  }

  private BinaryContent findEntityOrThrow(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}