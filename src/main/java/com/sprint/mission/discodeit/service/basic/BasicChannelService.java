package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForReadableInstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC,
            request.name(),
            request.description()
    );
    return channelRepository.save(channel);
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, "", "");
    Channel createdChannel = channelRepository.save(channel);

//    request.participantIds().stream()
//        .map(userId -> new ReadStatus(userId, createdChannel.getId()))
//        .forEach(readStatusRepository::save);
//
//    return createdChannel;
    List<User> participants = request.participantIds().stream()
            .map(userId -> userRepository.findById(userId)
                    .orElseThrow(()-> new NoSuchElementException("User with id " + userId +  "not found")))
            .toList();
    List<ReadStatus> readStatuses = participants.stream()
            .map(user->new ReadStatus(user, createdChannel, Instant.now()))
            .toList();
    readStatusRepository.saveAll(readStatuses);
    return createdChannel;
  }

  @Transactional(readOnly = true)
  @Override
  public Channel find(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public List<Channel> findAllByUser_Id(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
                (channel.getType() == ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(request.newName(), request.newDescription());
    //Dirty Checking
    return channel;
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());

    channelRepository.delete(channel);
  }


}
