package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(getParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    // ChannelDto에 입력될 LastMessageAt을 가져 오기 위한 protected 메서드
    // MapStruct가 생성하는 구현체 클래스에서 접근 가능해야 하기 때문에 protected를 사용해야 한다고 함
    protected Instant getLastMessageAt(Channel channel) {
        Optional<Message> lastMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId());
        return lastMessage.map(Message::getCreatedAt).orElse(null);
    }

    // ChannelDto에 입력될 participants를 가져 오기 위한 protected 메서드
    // MapStruct가 생성하는 구현체 클래스에서 접근 가능해야 하기 때문에 protected를 사용해야 한다고 함
    // PRIVATE 채널인 경우는 user에 대한 정보가 포함 되어야 함
    protected List<UserDto> getParticipants(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannel(channel)
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .toList();
        }
        return participants;
    }
}
