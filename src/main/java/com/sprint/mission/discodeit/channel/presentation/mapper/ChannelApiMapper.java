package com.sprint.mission.discodeit.channel.presentation.mapper;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.channel.presentation.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.presentation.mapper.UserApiMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class, uses = UserApiMapper.class)
public interface ChannelApiMapper extends GenericApiMapper<ChannelDetailDto, ChannelResponse> {

  ChannelResponse toDto(Channel channel);

  @Override
  @Mapping(target = ".", source = "channel")
  ChannelResponse toDto(ChannelDetailDto entity);
}
