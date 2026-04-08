package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.common.CommonErrorCode;
import com.sprint.mission.discodeit.exception.common.CommonException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService extends BasicDomainService<BinaryContent>
    implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentMapper.toDto(binaryContentRepository.findAllById(ids));
  }

  @Override
  public BinaryContentDto create(MultipartFile file) {
    BinaryContent content = getBinaryContent(file);
    binaryContentRepository.save(content);
    return binaryContentMapper.toDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID id) {
    return binaryContentMapper.toDto(findById(id));
  }

  @Override
  public void delete(UUID id) {
    deleteByIdOrThrow(id, binaryContentRepository,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND,
            value));
  }

  @Override
  protected BinaryContent findById(UUID id) {
    return getOrThrow(id, binaryContentRepository::findById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND, value));
  }

  private BinaryContent getBinaryContent(MultipartFile profile) {
    try {
      return binaryContentMapper.toEntityFrom(profile);
    } catch (IOException e) {
      log.error("[BinaryContent] file can't be loaded");
      throw new CommonException(CommonErrorCode.FILE_CANT_READ, profile);
    }
  }
}
