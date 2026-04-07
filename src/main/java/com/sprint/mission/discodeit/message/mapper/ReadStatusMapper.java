package com.sprint.mission.discodeit.message.mapper;

import com.sprint.mission.discodeit.message.dto.ReadStatusDto;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "channel.id", target = "channelId")
  ReadStatusDto toDto(ReadStatus readStatus);
}