package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  BinaryContentResponse toResponse(BinaryContent binaryContent);

  BinaryContentDto toDto(BinaryContent binaryContent);

  List<BinaryContentDto> toDtoList(List<BinaryContent> binaryContents);

  BinaryContentDto toDto(BinaryContentResponse binaryContentResponse);
}