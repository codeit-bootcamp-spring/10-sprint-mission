package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class BinaryContentMapper {

    @Mapping(target = "bytes", ignore = true)
    public abstract BinaryContentDto toDto(BinaryContent binaryContent);
}