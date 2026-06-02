package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    @Mapping(target = "bytes", expression = "java(new byte[0])")
    BinaryContentDto toDto(BinaryContent binaryContent);
}
