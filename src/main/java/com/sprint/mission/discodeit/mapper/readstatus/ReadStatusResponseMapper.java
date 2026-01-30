package com.sprint.mission.discodeit.mapper.readstatus;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadStatusResponseMapper {
    ReadStatusResponseDto toDto(ReadStatus readStatusResponseDto);

}
