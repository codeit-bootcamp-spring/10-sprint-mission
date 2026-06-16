package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Mapper(componentModel = "spring", uses = {UserService.class, ReadStatusRepository.class, MessageRepository.class})
public abstract class ChannelMapper {
    @Autowired
    protected  ReadStatusRepository readStatusRepository;
    @Autowired
    protected  MessageRepository messageRepository;
    @Autowired
    private UserService userService;

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
                .map(readStatus -> userService.findUser(readStatus.getUser().getId()))
                .toList();
    }
}
