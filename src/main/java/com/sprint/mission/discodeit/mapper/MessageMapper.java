package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "channelId", source = "channel.id")
    MessageDto toDto(MessageEntity message);

    // 생성 요청 DTO -> 엔티티 변환
    @Mapping(target = "content", source = "messageCreateRequest.content")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "channel", source = "channel")
    MessageEntity toEntity(MessageCreateRequest messageCreateRequest, UserEntity author, ChannelEntity channel);
}
