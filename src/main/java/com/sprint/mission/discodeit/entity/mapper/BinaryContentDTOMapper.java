package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentDTOMapper {
    public BinaryContentResponseDTO binaryContentToResponse(BinaryContent binaryContent){
        return new BinaryContentResponseDTO(binaryContent.getId(), binaryContent.getContentType(), binaryContent.getCreatedAt());
    }

    public BinaryContent requestToBinaryContent(BinaryContentCreateRequestDTO req){
        return new BinaryContent(req.contentType(), req.file());
    }
}
