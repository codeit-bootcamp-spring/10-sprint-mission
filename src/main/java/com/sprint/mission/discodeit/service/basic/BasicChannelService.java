package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
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
    private final ChannelMapper channelMapper;

    @Override
    public ChannelInfoDto createPublic(PublicChannelCreateDto publicChannelCreateDto) {
       userRepository.findById(publicChannelCreateDto.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel =
                new Channel(publicChannelCreateDto.name(), IsPrivate.PUBLIC, publicChannelCreateDto.ownerId(), publicChannelCreateDto.description());
        ReadStatus readStatus = new ReadStatus(publicChannelCreateDto.ownerId(), channel.getId());
        readStatusRepository.save(readStatus);
        channelRepository.save(channel);
        userStatusRepository.findByUserId(publicChannelCreateDto.ownerId())
                .ifPresent(UserStatus::updateLastActiveTime);
        return channelMapper.toChannelInfoDto(channel, messageRepository);
    }

    @Override
    public ChannelInfoDto createPrivate(PrivateChannelCreateDto privateChannelCreateDto) {
        userRepository.findById(privateChannelCreateDto.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel =
                new Channel(null, IsPrivate.PRIVATE, privateChannelCreateDto.ownerId(), null);

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
        return channelMapper.toChannelInfoDto(channel, messageRepository);
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
    public ChannelInfoDto update(ChannelUpdateDto channelUpdateDto) {
        Channel channel = channelRepository.findById(channelUpdateDto.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));
        if (channel.getIsPrivate().equals(IsPrivate.PRIVATE)) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.updateName(channelUpdateDto.name());
        channel.updatePrivate(channelUpdateDto.isPrivate());
        channel.updateOwnerId(channelUpdateDto.ownerId());
        channelRepository.save(channel);
        return channelMapper.toChannelInfoDto(channel, messageRepository);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        channel.addUserId(userId);
        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);
        channelRepository.save(channel);
        userRepository.save(user);
        Objects.requireNonNull(userStatusRepository.findByUserId(userId).orElse(null)).updateLastActiveTime();

    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));

        // 채널의 메시지 삭제하기
        messageRepository.deleteByChannelId(id);

        // ReadStatus 삭제
        readStatusRepository.deleteByChannelId(id);
        channelRepository.delete(id);
    }

    @Override
    public void clear() {
        channelRepository.clear();
    }

}
