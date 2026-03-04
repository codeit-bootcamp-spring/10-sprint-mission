package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BinaryContentMapper {

    BinaryContent toEntity(CreateBinaryContentPayloadDTO dto);

    BinaryContentResponseDTO toResponse(BinaryContent binaryContent);

    List<BinaryContentResponseDTO> toResponseList(List<BinaryContent> binaryContents);
}
