package com.sprint.mission.discodeit.mapper.binarycontent;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentResponseMapper {
    BinaryContentResponseDto toDto(BinaryContent binaryContent);
}
