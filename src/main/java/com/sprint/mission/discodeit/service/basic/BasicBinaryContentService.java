package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentDto create(MultipartFile multipartFile) {
    try {
      BinaryContent binaryContent = new BinaryContent(
          multipartFile.getOriginalFilename(),
          multipartFile.getSize(),
          multipartFile.getContentType()
      );
      //DB에 메타데이터 저장
      binaryContentRepository.save(binaryContent);
      //실제 byte[] 저장
      binaryContentStorage.put(binaryContent.getId(), multipartFile.getBytes());
      return binaryContentMapper.toDto(binaryContent);
    } catch (IOException e) {
      throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
    }
  }

  @Override
  public BinaryContentDto findById(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> contentsIds) {
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
