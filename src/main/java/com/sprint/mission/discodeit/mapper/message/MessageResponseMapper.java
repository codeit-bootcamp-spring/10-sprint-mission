package com.sprint.mission.discodeit.mapper.message;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageResponseMapper {
    MessageResponseDto toDto(Message message);
}
