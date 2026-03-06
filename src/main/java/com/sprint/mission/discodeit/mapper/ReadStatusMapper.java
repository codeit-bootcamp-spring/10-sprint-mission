package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ReadStatusMapper {
    @Mapping(source = "user.getId()", target = "userId")
    @Mapping(source = "channel.getId()", target = "channelId")
    public abstract ReadStatusDto toDto(ReadStatus readStatus);
}
