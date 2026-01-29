package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChannelMapper {
    ChannelDto.Response toResponse(Channel channel, List<UUID> memberIds);
}
