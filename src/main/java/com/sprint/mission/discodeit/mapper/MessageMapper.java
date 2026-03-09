package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

    @Mapping(source = "channel.id",target = "channelId")//필드 이름이 달라서 @Mapping을 통해 써준다.
    MessageDto toDto(Message message);
}
