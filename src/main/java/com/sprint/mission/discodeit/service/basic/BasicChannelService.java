package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ChannelDto createPublic(PublicChannelCreateRequest publicChannelCreateRequest) {
        log.info("PUBLIC 채널 생성 시작: name={}", publicChannelCreateRequest.name());

        Channel channel =
                new Channel(publicChannelCreateRequest.name(), IsPrivate.PUBLIC,
                        publicChannelCreateRequest.description());

        channelRepository.save(channel);
        log.info("PUBLIC 채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());

        return channelMapper.toChannelDto(channel);
    }

    @Override
    @Transactional
    public ChannelDto createPrivate(PrivateChannelCreateRequest privateChannelCreateRequest) {
        log.info("PRIVATE 채널 생성 시작: participantIds={}",
                privateChannelCreateRequest.participantIds());

        // 1. 참여자 유효성 먼저 확인 (리스트로 미리 가져오기)
        List<User> participants = privateChannelCreateRequest.participantIds().stream()
                .map(uId -> userRepository.findById(uId)
                        .orElseThrow(() -> {
                            log.warn("PRIVATE 채널 생성 실패 - 존재하지 않는 사용자: id={}", uId);
                            return new UserNotFoundException(uId);
                        }))
                .toList();

        // 2. 유저가 모두 확인된 후에 채널 생성 및 저장
        Channel channel = new Channel(null, IsPrivate.PRIVATE, null);
        Channel savedChannel = channelRepository.save(channel);

        // 3. ReadStatus 생성
        participants.forEach(user -> {
            ReadStatus readStatus = new ReadStatus(user, savedChannel, Instant.now());
            readStatusRepository.save(readStatus);
        });
        log.info("PRIVATE 채널 생성 완료: id={}", channel.getId());

        return channelMapper.toChannelDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));

        return channelMapper.toChannelDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAllByUserId(userId);
        if (channels.isEmpty()) {
            return List.of();
        }
        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, List<UserDto>> participantsMap = readStatusRepository
                .findAllByChannelIdIn(channelIds).stream()
                .collect(Collectors.groupingBy(
                        rs -> rs.getChannel().getId(),
                        Collectors.mapping(rs -> userMapper.toUserDto(rs.getUser()),
                                Collectors.toList())
                ));

        Map<UUID, Instant> lastMessageAtMap = messageRepository
                .findLastMessagesByChannelIds(channelIds).stream()
                .collect(Collectors.toMap(
                        m -> m.getChannel().getId(),
                        Message::getCreatedAt
                ));

        return channels.stream()
                .map(ch -> channelMapper.toChannelDto(
                        ch,
                        participantsMap.getOrDefault(ch.getId(), List.of()),
                        lastMessageAtMap.get(ch.getId())
                ))
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto update(UUID id, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        log.info("채널 수정 시작: id={}", id);

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("채널 수정 실패 - 존재하지 않는 채널: id={}", id);
                    return new ChannelNotFoundException(id);
                });

        if (channel.getType().equals(IsPrivate.PRIVATE)) {
            log.warn("채널 수정 실패 - PRIVATE 채널 수정 시도: id={}", id);
            throw new PrivateChannelUpdateNotAllowedException(id);
        }

        channel.updateName(publicChannelUpdateRequest.newName());
        channel.updateDescription(publicChannelUpdateRequest.newDescription());
        log.info("채널 수정 완료: id={}, name={}", id, channel.getName());

        return channelMapper.toChannelDto(channel);
    }

//  @Override
//  @Transactional
//  public ChannelDto joinChannel(UUID userId, UUID channelId) {
//    Channel channel = channelRepository.findById(channelId)
//        .orElseThrow(() -> new ChannelNotFoundException(channelId));
//    User user = userRepository.findById(userId)
//        .orElseThrow(() -> new UserNotFoundException(userId));
//
//    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
//    readStatusRepository.save(readStatus);
//
//    userStatusRepository.findByUserId(userId)
//        .ifPresent(status -> status.update(Instant.now()));
//
//    return channelMapper.toChannelDto(channel);
//  }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("채널 삭제 시작: id={}", id);

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("채널 삭제 실패 - 존재하지 않는 채널: id={}", id);
                    return new ChannelNotFoundException(id);
                });

        // 채널의 메시지 삭제하기
        messageRepository.deleteByChannelId(id);
        // ReadStatus 삭제
        readStatusRepository.deleteByChannelId(id);
        channelRepository.deleteById(id);
        log.info("채널 삭제 완료: id={}", id);
    }

}
