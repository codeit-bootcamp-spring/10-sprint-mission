package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.*;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.common.exception.channel.ChannelCantUpdatePrivateException;
import com.sprint.mission.discodeit.common.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.message.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {

  private final JPAChannelRepository jpaChannelRepository;
  private final JPAReadStatusRepository jpaReadStatusRepository;
  private final JPAMessageRepository jpaMessageRepository;
  private final JPAUserRepository jpaUserRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto create(ChannelCreatePrivateRequest request) {

    log.info("[CHANNEL_CREATE] private 채널 생성 시작 : participantIds={}",
        request.participantIds());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = jpaChannelRepository.save(channel);

    List<User> participants = jpaUserRepository.findAllById(request.participantIds());

    if (participants.isEmpty()) {
      throw new IllegalArgumentException("참여자가 없습니다.");
    } // 나중에 수정 필요

    List<ReadStatus> readStatuses = participants.stream()
        .map(user -> new ReadStatus(user, createdChannel, Instant.now()))
        .toList();

    jpaReadStatusRepository.saveAll(readStatuses);
    log.info("[CHANNEL_CREATE] private 채널 생성 완료 channelId={}", channel.getId());
    return channelMapper.toDto(createdChannel);
  }

  @Override
  @Transactional
  public ChannelDto create(ChannelCreatePublicRequest request) {
    log.info("[CHANNEL_CREATE] public 채널 생성 시작 : channelName={}, channelDescription={}",
        request.name(), request.description());

    if (request.name() == null || request.name().isBlank()) {
      throw new IllegalArgumentException("채널 이름은 필수입니다.");
    } // 나중에 수정 필요

    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    jpaChannelRepository.save(channel);

    log.info("[CHANNEL_CREATE] public 채널 생성 완료 : channelId={}",
        channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = jpaChannelRepository.findById(channelId)
        .orElseThrow(
            () -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findByUserId(UUID userId) {
    List<UUID> subscribedIds = jpaReadStatusRepository.findAllByUserId(userId)
        .stream()
        .map(rs -> rs.getChannel().getId())
        .toList();

    List<Channel> channels = jpaChannelRepository
        .findByTypeOrIdIn(ChannelType.PUBLIC, subscribedIds);
    List<UUID> channelIds = channels.stream()
        .map(Channel::getId)
        .toList();

    Map<UUID, Instant> lastMessageMap = jpaMessageRepository
        .findLastMessageTimesByChannelIds(channelIds)
        .stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> (Instant) row[1]
        ));

    Map<UUID, List<UserDto>> participantMap = jpaReadStatusRepository
        .findAllByChannelIdIn(channelIds)
        .stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    return channels.stream()
        .map(channel -> new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            participantMap.getOrDefault(channel.getId(), Collections.emptyList()),
            lastMessageMap.getOrDefault(channel.getId(), Instant.MIN)
        ))
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
    log.info("[CHANNEL_UPDATE] 채널 정보 수정 시작 : channelId={}",
        channelId);

    Channel channel = jpaChannelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new ChannelCantUpdatePrivateException(Map.of("channelId", channelId));
    }

    channel.update(request.newName(), request.newDescription());
    log.info("[CHANNEL_UPDATE] 채널 정보 수정 완료 : channelId={}, channelNewName={}, channelNewDesc={}"
        , channelId, channel.getName(), channel.getDescription());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {

    log.info("[CHANNEL_DELETE] 채널 삭제 시작 : channelId={} ", channelId);

    Channel channel = jpaChannelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    jpaChannelRepository.delete(channel);
    log.info("[CHANNEL_DELETE] 채널 삭제 완료 : channelId={} ", channelId);

  }
}
