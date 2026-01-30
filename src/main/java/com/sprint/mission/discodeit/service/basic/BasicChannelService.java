package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService, ClearMemory {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private final ChannelMapper channelMapper;

    @Override
    public Channel createPublic(PublicChannelCreateDto publicChannelCreateDto) {
        User user = userRepository.findById(publicChannelCreateDto.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel =
                new Channel(publicChannelCreateDto.name(), publicChannelCreateDto.isPrivate(), publicChannelCreateDto.ownerId(), publicChannelCreateDto.description());
        channelRepository.save(channel);
        userStatusRepository.findByUserId(publicChannelCreateDto.ownerId())
                .ifPresent(UserStatus::updateLastActiveTime);
        return channel;
    }

    @Override
    public Channel createPrivate(PrivateChannelCreateDto privateChannelCreateDto) {
        userRepository.findById(privateChannelCreateDto.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel =
                new Channel(null, privateChannelCreateDto.isPrivate(), privateChannelCreateDto.ownerId(), null);

        // ReadStatus 생성
        privateChannelCreateDto.users()
                .forEach(uId -> {
                    channel.addUserId(uId);
                    ReadStatus readStatus = new ReadStatus(uId, channel.getId());
                    readStatusRepository.save(readStatus);
                });

        channelRepository.save(channel);
        userStatusRepository.findByUserId(privateChannelCreateDto.ownerId())
                .ifPresent(UserStatus::updateLastActiveTime);
        return channel;
    }

    @Override
    public ChannelInfoDto findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다."));

        return channelMapper.toChannelInfoDto(channel, messageRepository);
    }

    @Override
    public List<ChannelInfoDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(ch -> isVisibleToUser(ch, userId))
                .map(ch -> channelMapper.toChannelInfoDto(ch, messageRepository))
                .toList();
    }

    private boolean isVisibleToUser(Channel channel, UUID userId){
        return channel.getIsPrivate().equals(IsPrivate.PUBLIC)
                || channel.getUserIds().contains(userId);
    }
    @Override
    public Channel update(UUID id, String name, IsPrivate isPrivate, UUID ownerId) {
        Channel channel = findById(id);
        channel.updateName(name);
        channel.updatePrivate(isPrivate);
        channel.updateOwnerId(ownerId);
        return channelRepository.save(channel);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = findById(channelId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        channel.addUserId(userId);
        channelRepository.save(channel);
        userRepository.save(user);
        userStatusRepository.findByUserId(userId).orElse(null).updateLastActiveTime();

    }

    @Override
    public List<UUID> getChannelMessageIds(UUID channelId) {
        findById(channelId);
        return messageRepository.readAll().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(msg -> msg.getId())
                .toList();
    }

    @Override
    public List<UUID> getChannelUserIds(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getUserIds();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        // 채널의 메시지 삭제하기
        List<UUID> messages = channel.getMessageIds();
        messages.forEach(messageRepository::delete);

        channelRepository.delete(id);
    }

    @Override
    public void clear() {
        channelRepository.clear();
    }

}
