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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentDto create(MultipartFile multipartFile) {
    try {
      log.debug("파일 업로드 시작: name={}, size={}", multipartFile.getOriginalFilename(),
          multipartFile.getSize());
      BinaryContent binaryContent = new BinaryContent(
          multipartFile.getOriginalFilename(),
          multipartFile.getSize(),
          multipartFile.getContentType()
      );
      //DB에 메타데이터 저장
      binaryContentRepository.save(binaryContent);
      //실제 byte[] 저장
      binaryContentStorage.put(binaryContent.getId(), multipartFile.getBytes());
      log.info("파일 저장 성공: binaryContentId={}", binaryContent.getId());
      return binaryContentMapper.toDto(binaryContent);
    } catch (IOException e) {
      throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto findById(UUID binaryContentId) {
    log.debug("파일 조회 시작: binaryContentId={}", binaryContentId);
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
    log.debug("파일 조회 완료: binaryContentId={}, fileName={}, size={}, contentType={}",
        binaryContent.getId(), binaryContent.getFileName(), binaryContent.getSize(),
        binaryContent.getContentType());
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    log.debug("파일 목록 조회 시작: binaryContentIdCount={}", ids.size());
    List<BinaryContent> allByIdIn = binaryContentRepository.findAllByIdIn(ids);
    log.debug("파일 목록 조회 완료: binaryContentCount={}", allByIdIn.size());
    return allByIdIn.stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    log.debug("파일 삭제 시작: binaryContentId={}", binaryContentId);
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
    binaryContentRepository.delete(binaryContent);
    log.info("파일 삭제 성공: binaryContentId={}", binaryContentId);
  }
}
