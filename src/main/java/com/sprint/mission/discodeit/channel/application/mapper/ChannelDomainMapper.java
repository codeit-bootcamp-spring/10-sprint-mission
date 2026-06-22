package com.sprint.mission.discodeit.channel.application.mapper;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PrivateChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PublicChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PublicChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.common.mapper.util.UtilMapper;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = UtilMapper.class)
public interface ChannelDomainMapper {

  Channel toEntityFrom(PublicChannelCreateRequest request);

  Channel toEntityFrom(PrivateChannelCreateRequest request);

  void partialUpdate(PublicChannelUpdateRequest dto, @MappingTarget Channel entity);

  PublicChannelCreatedEvent toCreatedEvent(Channel channel);

  PrivateChannelCreatedEvent toCreatedEvent(Channel channel, List<UUID> participantIds);

  ChannelUpdatedEvent toUpdatedEvent(Channel channel);

  PublicChannelDeletedEvent toDeletedEvent(Channel channel);

  @Mapping(target = "participantIds", source = "participants", qualifiedByName = "extractIds")
  PrivateChannelDeletedEvent toDeletedEvent(Channel channel, List<User> participants);
}
