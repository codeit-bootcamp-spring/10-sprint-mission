package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContentDto.Response create(MultipartFile multipartFile) {
    try {
      BinaryContent binaryContent = new BinaryContent(
          multipartFile.getOriginalFilename(),
          multipartFile.getContentType(),
          multipartFile.getSize(),
          multipartFile.getBytes()
      );
      binaryContentRepository.save(binaryContent);
      return BinaryContentDto.Response.of(binaryContent);
    } catch (IOException e) {
      throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
    }
  }

  @Override
  public BinaryContentDto.Response findById(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
    return BinaryContentDto.Response.of(binaryContent);
  }

  @Override
  public List<BinaryContentDto.Response> findAllByIdIn(List<UUID> contentsIds) {
    return contentsIds.stream()
        .map(this::findById)
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
    binaryContentRepository.delete(binaryContent);
  }
}
