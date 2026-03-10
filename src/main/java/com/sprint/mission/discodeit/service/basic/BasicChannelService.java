package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(
            ChannelType.PUBLIC,
            publicChannelCreateRequest.name(),
            publicChannelCreateRequest.description()
        );
        return channelMapper.toDto(channelRepository.save(channel));
    }

    // PrivateChannelCreateRequest에 유저 정보가 포함되어 있어야함
    // 유저별 ReadStatus 정보를 생성
    @Override
    public ChannelDto create(PrivateChannelCreateRequest privateChannelCreateRequest) {
        List<UUID> participantsIds = privateChannelCreateRequest.participantIds();
        // Channel 생성
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        // 유저별 ReadStatus 생성
        List<User> users = userRepository.findAllById(participantsIds);
        List<ReadStatus> readStatuses = users.stream()
                .map(user -> new ReadStatus(user, channel, Instant.now()))
                .toList();
        readStatusRepository.saveAll(readStatuses);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        Channel channel = getChannelByIdOrThrow(channelId);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        // userId를 가진 user가 참여 중인 PRIVATE 채널들(ReadStatus는 private 채널에 참여 중인 유저에 대한 정보와 최근 읽은 시간에 대한 정보를 가지고 있음)
        List<Channel> joinedChannels = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannel)
                .toList();

        return joinedChannels
                .stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = getChannelByIdOrThrow(channelId);
        // PRIVATE 채널은 수정 불가능
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정 불가능합니다");
        }
        channel.update(
                publicChannelUpdateRequest.newName(),
                publicChannelUpdateRequest.newDescription()
        );
        return channelMapper.toDto(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        // 관련 도메인 삭제(Message, ReadStatus)
        messageRepository.deleteAllByChannelId((channelId));
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    // channelRepository.findById()를 통한 반복되는 Channel 조회/예외처리를 중복제거 하기 위한 메서드
    private Channel getChannelByIdOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("channelId:"+channelId+"를 가진 채널을 찾을 수 없습니다"));
    }
}
