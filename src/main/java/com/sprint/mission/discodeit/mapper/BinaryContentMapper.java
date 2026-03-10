package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    @Mapping(target = "bytes",ignore = true) //Entity에서 bytes 삭제해서 DTO에 들어갈 bytes를 미룸.(Service단에서 넣어줘야함.)
    BinaryContentDto toDto(BinaryContent binaryContent);
}
