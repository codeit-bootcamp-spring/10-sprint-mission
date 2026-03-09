package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface ChannelMapper {

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true) // participants와 lastMessageAt은 Channel Entity에 없어서 Service단에서 채워주도록 미룸.
    ChannelDto toDto(Channel channel);
}
