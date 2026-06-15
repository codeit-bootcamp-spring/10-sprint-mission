package com.sprint.mission.discodeit.channel.presentation.mapper;

import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.common.mapper.GenericMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ChannelMapper extends GenericMapper<ChannelDetailDto, ChannelResponse> {

  Channel toEntityFrom(PublicChannelCreateRequest request);

  Channel toEntityFrom(PrivateChannelCreateRequest request);

  Channel partialUpdate(PublicChannelUpdateRequest request, @MappingTarget Channel channel);

  ChannelResponse toDtoFrom(Channel channel, List<User> participants);

  ChannelResponse toDto(Channel channel);

  @Override
  @Mapping(target = ".", source = "channel")
  ChannelResponse toDto(ChannelDetailDto entity);
}
