package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.logging.ServiceLogAround;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BasicDomainTemplate domainTemplate;

  @Override
  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentMapper.toDto(binaryContentRepository.findAllCompletedByIds(ids));
  }

  @Override
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
