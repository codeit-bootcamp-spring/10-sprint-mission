package com.sprint.mission.discodeit.binarycontent.application.service;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.event.FileUploadEvent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentException;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository repository;
  private final BinaryContentMapper mapper;
  private final ApplicationEventPublisher eventPublisher;

  @ServiceLogAround
  public List<BinaryContentDto> create(List<FileUploadRequest> request) {
    List<BinaryContent> entities = mapper.toEntityFrom(request);
    repository.saveAll(entities);
    publishFileUploadEvent(entities, request);
    return mapper.toDto(entities);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return mapper.toDto(repository.findAllById(ids));
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID id) {
    return mapper.toDto(findById(id));
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public void existsOrThrow(UUID id) {
    DomainServiceSupport.requireOrThrow(id, repository::existsSuccessById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND, value));
  }

  private void publishFileUploadEvent(List<BinaryContent> entities,
      List<FileUploadRequest> request) {
    Map<UUID, byte[]> data = new HashMap<>();
    for (int i = 0; i < entities.size(); i++) {
      data.put(entities.get(i).getId(), request.get(i).bytes());
    }
    eventPublisher.publishEvent(new FileUploadEvent(data));
  }

  private BinaryContent findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND,
            value));
  }
}
