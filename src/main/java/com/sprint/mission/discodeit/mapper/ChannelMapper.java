package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ReadStatusRepository.class, MessageRepository.class})
public abstract class ChannelMapper {
    @Autowired
    protected   UserMapper userMapper;
    @Autowired
    protected  ReadStatusRepository readStatusRepository;
    @Autowired
    protected  MessageRepository messageRepository;

    @Mapping(target = "lastMessageAt", expression = "java(getLatestMessageTime(channel.getId()))")
    @Mapping(target = "participants", expression = "java(getParticipants(channel.getId()))")
    public abstract ChannelDto toDto(Channel channel);

    protected Instant getLatestMessageTime(UUID channelId){
        // 가장 최근 메시지 찾기
        return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId)
                .map(BaseEntity::getCreatedAt)
                .orElse(null);
    }

    protected List<UserDto> getParticipants(UUID channelId){
        return readStatusRepository.findAllByChannelId(channelId).stream()
                .map(readStatus -> userMapper.toDto(readStatus.getUser(),true))
                .toList();
    }
}