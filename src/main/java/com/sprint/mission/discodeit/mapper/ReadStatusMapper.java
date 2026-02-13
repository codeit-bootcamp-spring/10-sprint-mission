package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    ReadStatusDto.Response toResponse(ReadStatus readStatus);
}
