package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentException;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
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
  private final DomainServiceSupport domainTemplate;

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
    return domainTemplate.getOrThrow(id, binaryContentRepository::findCompletedById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND,
            value));
  }
}
