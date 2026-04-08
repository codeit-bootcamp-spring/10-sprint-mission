package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
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
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper mapper;

  @Transactional
  @Override
  public BinaryContentDto create(MultipartFile attachment) throws IOException {
    log.debug("[Service] 첨부파일 생성 시작: attachment={}", attachment);
    if (attachment == null) {
      return null;  // TODO Custom exception으로 교체 필요
    }

    BinaryContent content = new BinaryContent(attachment.getOriginalFilename(),
        attachment.getSize(), attachment.getContentType());
    binaryContentRepository.save(content);
    log.debug("[Service] 첨부파일 저장 완료: contentId={}", content.getId());

    binaryContentStorage.put(content.getId(), attachment.getBytes());
    log.info("[Service] 첨부파일 물리적 생성 성공: contentId={}, contentType={}",
        content.getId(), content.getContentType());
    return toResponse(content);
  }

  @Override
  public BinaryContentDto findById(UUID uuid) {
    return binaryContentRepository.findById(uuid)
        .map(this::toResponse)
        .orElseThrow(() -> new BinaryContentNotFoundException());
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> uuids) {
    return binaryContentRepository.findAllByIdIn(uuids).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public void deleteById(UUID uuid) throws IOException {
    log.debug("[Service] 첨부파일 삭제 시작: id={}", uuid);

    binaryContentRepository.findById(uuid)
        .orElseThrow(() -> new BinaryContentNotFoundException());
    binaryContentStorage.delete(uuid);
    log.debug("[Service] 첨부파일 물리적 삭제 완료: id={}", uuid);

    binaryContentRepository.deleteById(uuid);
    log.info("[Service] 첨부파일 삭제 성공: id={}", uuid);
  }

  private BinaryContentDto toResponse(BinaryContent binaryContent) {
    return mapper.toDto(binaryContent);
  }
}
