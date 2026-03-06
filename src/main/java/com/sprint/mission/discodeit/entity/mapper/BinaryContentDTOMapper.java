package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BinaryContentDTOMapper {

    public static Optional<BinaryContentDto> multipartToResponseDto(MultipartFile multipartFile) {
        if (multipartFile != null) {
            try {
                return Optional.of(
                    new BinaryContentDto(UUID.randomUUID(), Instant.now(), multipartFile.getName(),
                        multipartFile.getSize(),
                        multipartFile.getContentType(), multipartFile.getBytes()));
            } catch (IOException e) {
                throw new IllegalStateException("해당 파일을 읽을 수 없습니다!");
            }
        }
        return Optional.empty();
    }


    public static BinaryContentDto binaryContentToResponse(BinaryContent binaryContent) {
        return new BinaryContentDto(
            binaryContent.getId(),
            binaryContent.getCreatedAt(),
            binaryContent.getFileName(),
            binaryContent.getSize(),
            binaryContent.getContentType(),
            binaryContent.getBytes()
        );

    }
}
