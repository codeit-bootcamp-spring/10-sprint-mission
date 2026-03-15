package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    Channel saved = channelRepository.save(
            new Channel(ChannelType.PUBLIC, request.name(), request.description())
    );
    return buildChannelDto(saved);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    List<User> participants = request.participantIds().stream()
            .map(userId -> userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")))
            .toList();

    participants.stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .forEach(readStatusRepository::save);

    return buildChannelDto(channel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
            .map(this::buildChannelDto)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
            .map(ReadStatus::getChannelId)
            .toList();

    List<Channel> channels = channelRepository.findAll().stream()
            .filter(channel -> channel.getType() == ChannelType.PUBLIC || mySubscribedChannelIds.contains(channel.getId()))
            .toList();

    return buildChannelDtos(channels);
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    channel.update(request.newName(), request.newDescription());
    return buildChannelDto(channel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " not found");
    }
    messageRepository.deleteAllByChannel_Id(channelId);
    readStatusRepository.deleteAllByChannel_Id(channelId);
    channelRepository.deleteById(channelId);
  }

  private ChannelDto buildChannelDto(Channel channel) {
    return buildChannelDtos(List.of(channel)).get(0);
  }

  private List<ChannelDto> buildChannelDtos(List<Channel> channels) {
    if (channels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = channels.stream()
            .map(Channel::getId)
            .toList();

    Map<UUID, List<UserDto>> participantsMap = buildParticipantsMap(channelIds);
    Map<UUID, Instant> lastMessageAtMap = buildLastMessageAtMap(channelIds);

    return channels.stream()
            .map(channel -> channelMapper.toDto(
                    channel,
                    participantsMap.getOrDefault(channel.getId(), List.of()),
                    lastMessageAtMap.get(channel.getId())
            ))
            .toList();
  }

  private Map<UUID, List<UserDto>> buildParticipantsMap(Collection<UUID> channelIds) {
    Map<UUID, LinkedHashMap<UUID, UserDto>> temp = new LinkedHashMap<>();

    for (ReadStatus readStatus : readStatusRepository.findAllByChannel_IdIn(channelIds)) {
      UUID channelId = readStatus.getChannelId();
      User user = readStatus.getUser();

      temp.computeIfAbsent(channelId, id -> new LinkedHashMap<>())
              .putIfAbsent(user.getId(), userMapper.toDto(user));
    }

    return temp.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().values().stream().toList()
            ));
  }

  private Map<UUID, Instant> buildLastMessageAtMap(Collection<UUID> channelIds) {
    return messageRepository.findLastMessageAtByChannelIds(channelIds).stream()
            .collect(Collectors.toMap(
                    MessageRepository.ChannelLastMessageAtProjection::getChannelId,
                    MessageRepository.ChannelLastMessageAtProjection::getLastMessageAt
            ));
  }
}