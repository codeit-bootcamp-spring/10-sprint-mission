package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    @Mapping(target = "userId", source = "user")
    @Mapping(target = "channelId", source = "channel")
    ReadStatusDto toDto(ReadStatus status);

    default UUID userGetId(User user) {
        return user.getId();
    }

    default UUID channelGetId(Channel channel) {
        return channel.getId();
    }
}
