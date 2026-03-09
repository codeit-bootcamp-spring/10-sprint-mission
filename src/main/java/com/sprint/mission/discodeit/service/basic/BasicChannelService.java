package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
    Channel channel = new Channel(
        request.name(),
        request.description(),
        ChannelType.PUBLIC
    );
    channelRepository.save(channel);
    return toDto(channel, null, null);
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(null, null, ChannelType.PRIVATE);
    channelRepository.save(channel);

    // 참여 유저별 ReadStatu 생성
    request.participantIds().forEach(userId -> {
      User user = getOrThrowUser(userId);

      ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
      readStatusRepository.save(readStatus);
    });

    return toDto(channel, null, request.participantIds());
  }

  @Override
  public ChannelDto findById(UUID id) {
    Channel channel = getOrThrowChannel(id);
    return toDto(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // PUBLIC 채널 조회
    List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);
    // PRIVATE 채널 조회
    List<Channel> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .filter(channel -> channel.getType() == ChannelType.PRIVATE)
        .toList();

    return Stream.concat(publicChannels.stream(), privateChannels.stream())
        .map(channel -> toDto(channel, getLastMessageAt(channel.getId()),
            getMemberIdsIfPrivate(channel)))
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID id, PublicChannelUpdateRequest request) {
    Channel channel = getOrThrowChannel(id);

    // PRIVATE 채널은 수정할 수 없음
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    Optional.ofNullable(request.newName()).ifPresent(channel::updateName);
    Optional.ofNullable(request.newDescription()).ifPresent(channel::updateDescription);

    return toDto(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    Channel channel = getOrThrowChannel(id);
    channelRepository.delete(channel);
  }

  // --- Helper Methods ---

  // 특정 채널의 가장 최근 메시지 시각 조회
  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

  // 비공개 채널인 경우 참여자 ID 목록 조회
  private List<UUID> getMemberIdsIfPrivate(Channel channel) {
    if (channel.getType() != ChannelType.PRIVATE) {
      return null;
    }
    return readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(rs -> rs.getUser().getId())
        .toList();
  }

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

  // 엔티티 -> DTO 변환
  private ChannelDto toDto(Channel channel, Instant lastMessageAt,
      List<UUID> participantIds) {
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt,
        channel.getCreatedAt(),
        channel.getUpdatedAt()
    );
  }
}
