package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper{

    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    protected ReadStatusRepository readStatusRepository;
    @Autowired
    protected UserRepository userRepository;

    // id, type, name, desscription ok.
    // participants, lastMessageAt 계산 필요
    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(mapParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(mapLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    protected List<UserDto> mapParticipants(Channel channel){
        if(channel == null) return List.of();
        if(channel.getType() == ChannelType.PUBLIC) return List.of();

        List<UUID> userIds = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
                .map(readStatus -> readStatus.getUser().getId())
                .toList();

        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }
    //LastMessageAt 계산
    protected Instant mapLastMesssageAt(Channel channel){
        if(channel == null) return Instant.MIN;

        return messageRepository.findAllByChannel_Id(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(Instant.MIN);
    }


}