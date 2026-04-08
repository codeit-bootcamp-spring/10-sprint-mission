package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.exception.channel.ChannelAccessDeniedException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotUpdatableException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
    log.debug("Public channel creation requested: {}", request.getName());
    if (channelRepository.existsByName(request.getName())) {
      log.warn("Channel creation failed - Name already exists: {}", request.getName());
      throw new DuplicateChannelNameException(request.getName());
    }

    Channel channel = new Channel(request.getName(), ChannelType.PUBLIC, request.getDescription());
    channelRepository.save(channel);
    log.info("Public channel created successfully: id={}, name={}", channel.getId(), channel.getName());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
    log.debug("Private channel creation requested by creator: {}", request.getCreatorId());
    String defaultName = "Private Channel " + UUID.randomUUID().toString().substring(0, 8);
    Channel channel = new Channel(defaultName, ChannelType.PRIVATE, "Private message channel");
    channelRepository.save(channel);

    Set<UUID> participantIds = new HashSet<>();
    if (request.getParticipantIds() != null) {
      participantIds.addAll(request.getParticipantIds());
    }

    if (request.getCreatorId() != null) {
      participantIds.add(request.getCreatorId());
    }

    for (UUID userId : participantIds) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> {
            log.warn("Private channel creation failed - Participant not found: id={}", userId);
            return new UserNotFoundException(userId);
          });

      ReadStatus readStatus = new ReadStatus(user, channel);
      readStatusRepository.save(readStatus);
      channel.addReadStatus(readStatus);
      user.addReadStatus(readStatus);
    }
    log.info("Private channel created successfully: id={}, participants={}", channel.getId(), participantIds.size());
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto getChannel(UUID id) {
    log.debug("Fetching channel details: id={}", id);
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Channel not found: id={}", id);
          return new ChannelNotFoundException(id);
        });
    return channelMapper.toDto(channel);
  }

  @Override
  public List<ChannelDto> getAllChannels() {
    log.debug("Fetching all channels");
    return channelRepository.findAll().stream()
        .map(channelMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("Fetching all channels for user: id={}", userId);
    if (!userRepository.existsById(userId)) {
      log.warn("Fetching failed - User not found: id={}", userId);
      throw new UserNotFoundException(userId);
    }

    return channelRepository.findAll().stream()
        .filter(channel -> canUserAccessChannel(channel, userId))
        .map(channelMapper::toDto)
        .sorted(Comparator.comparing(ChannelDto::lastMessageAt,
            Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.toList());
  }

  private boolean canUserAccessChannel(Channel channel, UUID userId) {
    if (ChannelType.PUBLIC.equals(channel.getType())) {
      return true;
    }
    return readStatusRepository.findByUser_IdAndChannel_Id(userId, channel.getId()).isPresent();
  }

  @Override
  @Transactional
  public ChannelDto updateChannel(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("Channel update requested: id={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("Update failed - Channel not found: id={}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    if (ChannelType.PRIVATE.equals(channel.getType())) {
      log.warn("Update failed - Attempted to update PRIVATE channel: id={}", channelId);
      throw new PrivateChannelNotUpdatableException(channelId);
    }

    if (request.getNewName() != null && !request.getNewName().isBlank()) {
        channel.updateName(request.getNewName());
    }
    if (request.getNewDescription() != null) {
        channel.updateDescription(request.getNewDescription());
    }

    log.info("Channel updated successfully: id={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void deleteChannel(UUID id) {
    log.debug("Channel deletion requested: id={}", id);
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Deletion failed - Channel not found: id={}", id);
          return new ChannelNotFoundException(id);
        });

    channelRepository.delete(channel);
    log.info("Channel deleted successfully: id={}", id);
  }

  @Override
  @Transactional
  public ChannelDto enterChannel(UUID userId, UUID channelId) {
    log.debug("User entering channel: userId={}, channelId={}", userId, channelId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("Entry failed - User not found: id={}", userId);
          return new UserNotFoundException(userId);
        });
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("Entry failed - Channel not found: id={}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    if (ChannelType.PRIVATE.equals(channel.getType())) {
      log.warn("Entry failed - Attempted to enter PRIVATE channel without invitation: userId={}, channelId={}", userId, channelId);
      throw new ChannelAccessDeniedException(userId, channelId);
    }

    if (readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId).isPresent()) {
      log.warn("Entry failed - User already in channel: userId={}, channelId={}", userId, channelId);
      throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
    }

    ReadStatus readStatus = new ReadStatus(user, channel);
    readStatusRepository.save(readStatus);
    channel.addReadStatus(readStatus);
    user.addReadStatus(readStatus);

    log.info("User entered channel: userId={}, channelId={}", userId, channelId);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void leaveChannel(UUID userId, UUID channelId) {
    log.debug("User leaving channel: userId={}, channelId={}", userId, channelId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)
        .ifPresent(readStatus -> {
          readStatusRepository.delete(readStatus);
          log.info("User left channel: userId={}, channelId={}", userId, channelId);
        });
  }
}
