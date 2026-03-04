package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BinaryContentDTOMapper {

    public static BinaryContentResponseDTO binaryContentToResponse(BinaryContent binaryContent) {
        return new BinaryContentResponseDTO(binaryContent.getId(), binaryContent.getContentType(),
            binaryContent.getCreatedAt(), binaryContent.getFile());
    }

    public static Optional<BinaryContentDTO> multipartToResponseDto(MultipartFile multipartFile) {
        if (multipartFile != null) {
            try {
                return Optional.of(
                    new BinaryContentDTO(UUID.randomUUID(), Instant.now(), multipartFile.getName(),
                        multipartFile.getSize(),
                        multipartFile.getContentType(), multipartFile.getBytes()));
            } catch (IOException e) {
                throw new IllegalStateException("해당 파일을 읽을 수 없습니다!");
            }
        }
        return Optional.empty();
    }

    public static BinaryContent requestToBinaryContent(BinaryContentCreateRequestDTO req) {
        return new BinaryContent(req.contentType(), req.file());
    }
}
