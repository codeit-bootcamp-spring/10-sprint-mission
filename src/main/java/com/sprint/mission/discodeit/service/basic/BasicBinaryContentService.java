package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContentDto create(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("파일이 비어있습니다.");
    }

    try {
      BinaryContent binaryContent = new BinaryContent(
          file.getOriginalFilename(),
          file.getSize(),
          file.getContentType(),
          file.getBytes()
      );

      binaryContentRepository.save(binaryContent);
      return toDto(binaryContent);

    } catch (IOException e) {
      throw new RuntimeException("파일 데이터를 읽는 중 오류가 발생했습니다.", e);
    }
  }

  @Override
  public BinaryContentDto findById(UUID id) {
    return toDto(getOrThrowBinaryContent(id));
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids).stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    getOrThrowBinaryContent(id);
    binaryContentRepository.deleteById(id);
  }

  // BinaryContentController에서 파일 조회할 때 엔티티를 반환하기 위해 만든 메서드
  @Override
  public BinaryContent findEntity(UUID id) {
    return getOrThrowBinaryContent(id);
  }

  @Override
  public List<BinaryContent> findEntities(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids);
  }


  // 바이너리 컨텐츠 검증
  private BinaryContent getOrThrowBinaryContent(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 바이너리 콘텐츠를 찾을 수 없습니다."));
  }

  // 엔티티 -> DTO 변환
  private BinaryContentDto toDto(BinaryContent binaryContent) {
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        binaryContent.getBytes(),
        binaryContent.getCreatedAt()
    );
  }

}
