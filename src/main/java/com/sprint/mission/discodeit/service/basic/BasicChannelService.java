package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  //
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelRepository.save(channel);
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    request.participantIds().stream()
        .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  @Override
  public ChannelResponse find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toResponse)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId)
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ChannelResponse findWithDetails(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    Instant lastMessageTime = messageRepository.findAllByChannelId(channelId).stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    if (channel.getType() == ChannelType.PRIVATE) {
      List<UUID> participantUserIds = readStatusRepository.findAllByChannelId(channelId).stream()
          .filter(rs -> rs.getChannelId().equals(channelId))
          .map(ReadStatus::getUserId)
          .collect(Collectors.toList());
      return ChannelResponse.from(channel, lastMessageTime, participantUserIds);
    }

    return ChannelResponse.from(channel, lastMessageTime);
  }


  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(channelId);
  }


  private ChannelResponse toResponse(Channel channel) {

    Instant lastMessagenAt = messageRepository.findAllByChannelId(channel.getId())
        .stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(ReadStatus::getUserId)
          .collect(Collectors.toList());
      return ChannelResponse.from(channel, lastMessagenAt, participantIds);
    }
    return ChannelResponse.from(channel, lastMessagenAt);
  }

//  private ChannelDto toDto(Channel channel) {
//    Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
//        .stream()
//        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
//        .map(Message::getCreatedAt)
//        .limit(1)
//        .findFirst()
//        .orElse(Instant.MIN);
//
//    List<UUID> participantIds = new ArrayList<>();
//    if (channel.getType().equals(ChannelType.PRIVATE)) {
//      readStatusRepository.findAllByChannelId(channel.getId())
//          .stream()
//          .map(ReadStatus::getUserId)
//          .forEach(participantIds::add);
//    }
//
//    return new ChannelDto(
//        channel.getId(),
//        channel.getType(),
//        channel.getName(),
//        channel.getDescription(),
//        participantIds,
//        lastMessageAt
//    );
//  }
}
