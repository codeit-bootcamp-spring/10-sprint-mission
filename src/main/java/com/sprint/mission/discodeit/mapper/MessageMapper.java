package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// uses: MapStruct가 자동 매핑할 때, BinaryContentMapper, UserMapper 사용해도 된다는 옵션
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {
    @Mapping(target = "channelId", source = "channel.id")
    MessageDto toDto(Message message);
}
