package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ChannelMapper extends BaseMapper<Channel, ChannelDto> {

  Channel toEntityFrom(PublicChannelCreateRequest request);

  Channel toEntityFrom(PrivateChannelCreateRequest request);

  Channel partialUpdate(PublicChannelUpdateRequest request, @MappingTarget Channel channel);
}
