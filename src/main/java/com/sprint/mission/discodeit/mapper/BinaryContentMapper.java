package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  public BinaryContent fromDto(BinaryContentPostDto binaryContentPostDto) {
    return new BinaryContent(
        binaryContentPostDto.userId(),
        binaryContentPostDto.messageId(),
        binaryContentPostDto.fileName(),
        0,
        "",
        binaryContentPostDto.data()
    );
  }

  public BinaryContentResponseDto toResponseDto(BinaryContent binaryContent) {
    return new BinaryContentResponseDto(
        binaryContent.getId(),
        binaryContent.getCreatedAt(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        Base64.getEncoder().encodeToString(binaryContent.getBytes())
    );
  }

}
