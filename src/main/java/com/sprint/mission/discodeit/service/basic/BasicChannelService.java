package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public Channel createPublicChannel(String name, String description) {
    Channel channel = new Channel(name, description, ChannelType.PUBLIC);
    return channelRepository.save(channel);
  }

  @Override
  @Transactional
  public Channel createPrivateChannel(List<UUID> participantIds) {
    Channel channel = new Channel(null, null, ChannelType.PRIVATE);
    channelRepository.save(channel);

    // 참여 유저별 ReadStatu 생성
    participantIds.forEach(userId -> {
      User user = getOrThrowUser(userId);

      ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
      readStatusRepository.save(readStatus);
    });

    return channel;
  }

  @Override
  public Channel findById(UUID id) {
    return getOrThrowChannel(id);
  }

  @Override
  public List<Channel> findAllByUserId(UUID userId) {
    // PUBLIC 채널 조회
    List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);
    // PRIVATE 채널 조회
    List<Channel> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .filter(channel -> channel.getType() == ChannelType.PRIVATE)
        .toList();

    return Stream.concat(publicChannels.stream(), privateChannels.stream()).toList();
  }

  @Override
  @Transactional
  public Channel update(UUID id, String newName, String newDescription) {
    Channel channel = getOrThrowChannel(id);

    // PRIVATE 채널은 수정할 수 없음
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    Optional.ofNullable(newName).ifPresent(channel::updateName);
    Optional.ofNullable(newDescription).ifPresent(channel::updateDescription);

    return channel;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    Channel channel = getOrThrowChannel(id);
    channelRepository.delete(channel);
  }

  // --- Helper Methods ---

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
  }

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
  }
}
