package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  private final BinaryContentMapper binaryContentMapper;

  public BinaryContent create(BinaryContentPostDto binaryContentPostDto) {
    return binaryContentRepository.save(binaryContentMapper.fromDto(binaryContentPostDto));
  }

  public BinaryContentResponseDto findById(UUID id) throws IOException {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND, id)
        );

    File file = new File(
        Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "images",
            binaryContent.getFileName()).toString());

    return binaryContentMapper.toResponseDto(binaryContent);
  }

  public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList) {
    return binaryContentRepository.findByIdIn(idList).stream()
        .map(binaryContentMapper::toResponseDto)
        .collect(Collectors.toList());
  }

  public void delete(UUID id) {
    binaryContentRepository.findById(id)
        .ifPresentOrElse(
            value -> binaryContentRepository.delete(id),
            () -> {
              throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND, id);
            }
        );
  }
}
