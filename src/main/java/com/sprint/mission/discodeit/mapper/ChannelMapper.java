package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDetailResponse;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = UserMapper.class)
public interface ChannelMapper extends BaseMapper<ChannelDetailResponse, ChannelDto> {

  Channel toEntityFrom(PublicChannelCreateRequest request);

  Channel toEntityFrom(PrivateChannelCreateRequest request);

  Channel partialUpdate(PublicChannelUpdateRequest request, @MappingTarget Channel channel);

  ChannelDto toDtoFrom(Channel channel, List<User> participants);

  ChannelDto toDto(Channel channel);

  @Override
  @Mapping(target = ".", source = "channel")
  ChannelDto toDto(ChannelDetailResponse entity);
}
