package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentException;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.storage.event.FileUploadEventPublisher;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final FileUploadEventPublisher eventPublisher;
  private final String URL_TEMPLATE = "/binaryContents/%s";

  @ServiceLogAround
  public List<String> create(List<FileUploadRequest> request) {
    List<BinaryContent> entities = binaryContentMapper.toEntityFrom(request);
    binaryContentRepository.saveAll(entities);
    eventPublisher.publishAllFileUploadEvent(entities, request);
    return entities.stream().map(BinaryContent::getId).map(this::getUrl).toList();
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentMapper.toDto(binaryContentRepository.findAllCompletedByIds(ids));
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID id) {
    return binaryContentMapper.toDto(findById(id));
  }

  private BinaryContent findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, binaryContentRepository::findCompletedById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND,
            value));
  }

  private String getUrl(UUID id) {
    return URL_TEMPLATE.formatted(id);
  }
}
