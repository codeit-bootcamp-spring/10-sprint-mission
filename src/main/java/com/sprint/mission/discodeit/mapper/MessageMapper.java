package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MessageMapper {

    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "channelId", source = "message.channel.id")
    public MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                userMapper.toDto(message.getAuthor()),
                binaryContentMapper.toDtoList(message.getAttachments())
        );
    }

    public List<MessageDto> toDtoList(List<Message> messages) {
        List<MessageDto> dtos = new ArrayList<>();

        for (Message message: messages) {
            dtos.add(toDto(message));
        }

        return dtos;
    }
}
