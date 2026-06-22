package com.sprint.mission.discodeit.readstatus.presentation.mapper;

import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.presentation.dto.response.ReadStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface ReadStatusApiMapper extends GenericApiMapper<ReadStatus, ReadStatusResponse> {

  @Override
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusResponse toDto(ReadStatus entity);

}
