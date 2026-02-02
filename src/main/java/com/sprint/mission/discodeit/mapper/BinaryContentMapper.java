package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinaryContentMapper {
    public static BinaryContent toProfileEntity(UUID userId, CreateBinaryContentRequestDTO dto) {
        return new BinaryContent(
                userId,
                dto.data(),
                dto.contentType(),
                dto.filename()
        );
    }

    public static BinaryContent toMessageAttachmentEntity(UUID userId, UUID messageId, CreateBinaryContentRequestDTO dto) {
        return new BinaryContent(
                userId,
                messageId,
                dto.data(),
                dto.contentType(),
                dto.filename()
        );
    }

    public static BinaryContentResponseDTO toResponse(BinaryContent binaryContent) {
        return new BinaryContentResponseDTO(
                binaryContent.getId(),
                binaryContent.getUserId(),
                binaryContent.getMessageId(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getFilename(),
                binaryContent.getCreatedAt()
        );
    }

    public static List<BinaryContentResponseDTO> toResponseList(List<BinaryContent> binaryContents) {
        List<BinaryContentResponseDTO> responseDTOList = new ArrayList<>();

        for (BinaryContent bc: binaryContents) {
            responseDTOList.add(BinaryContentMapper.toResponse(bc));
        }

        return responseDTOList;
    }
}
