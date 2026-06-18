package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotUpdatableException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  public ChannelDto createPublicChannel(ChannelCreatePublicRequest request) {
    Channel newChannel = channelMapper.toEntity(request);
    Channel savedPublicChannel = channelRepository.save(newChannel);

    log.info("[SUCCESS] Created Public Channel: id={}, name={}",
        savedPublicChannel.getId(), newChannel.getName());

    ChannelDto channelDto =
        channelMapper.toDto(savedPublicChannel, List.of(), null);

    eventPublisher.publishEvent(
        new ChannelCreatedEvent(channelDto)
    );

    return channelDto;
  }

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  public ChannelDto createPrivateChannel(ChannelCreatePrivateRequest request) {
    List<User> participants = userRepository.findAllWithDetailsByIdIn(request.getParticipantIds());

    if (participants.size() != request.getParticipantIds().size()) {
      throw new UserNotFoundException(request.getParticipantIds());
    }

    Channel newChannel = new Channel(null, null, ChannelType.PRIVATE);

    participants.forEach(user -> {
      ReadStatus rs = new ReadStatus(user, newChannel, true);
      newChannel.addReadStatus(rs);
    });

    Channel savedChannel = channelRepository.save(newChannel);

    log.info("[SUCCESS] Created Private Channel: id={}, participantCount={}",
        savedChannel.getId(), participants.size());

    ChannelDto channelDto =
        channelMapper.toDto(
            savedChannel,
            participants,
            null
        );

    eventPublisher.publishEvent(
        new ChannelCreatedEvent(channelDto)
    );

    return channelDto;
  }

  @Override
  public ChannelDto findById(UUID channelId) {
    Channel channel = findChannelEntityById(channelId);

    // 단건 조회 시: 이 채널의 최신 메시지 시각을 레포지토리에서 딱 한 번 조회
    Instant lastMessageAt = messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
        .map(Message::getCreatedAt)
        .orElse(null);

    return channelMapper.toDto(channel, channel.getParticipants(), lastMessageAt);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "userChannels", key = "#userId")
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAllVisibleChannelsWithParticipants(userId);
    List<Object[]> lastMessageData = messageRepository.findAllLastMessageAt();

    Map<UUID, Instant> lastMessageMap = lastMessageData.stream()
        .collect(Collectors.toMap(
            obj -> (UUID) obj[0],
            obj -> (Instant) obj[1],
            (existing, replacement) -> existing // 중복 시 기존값 유지
        ));

    return channels.stream()
        .map(channel -> channelMapper.toDto(
            channel,
            channel.getParticipants(),
            lastMessageMap.get(channel.getId()) // 맵에서 꺼내면 끝!
        ))
        .toList();
  }

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
    Channel channel = findChannelEntityById(channelId);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelNotUpdatableException(channelId);
    }

    channel.update(request.getNewName(), request.getNewDescription());

    Instant lastMessageAt = messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
        .map(Message::getCreatedAt)
        .orElse(null);

    log.info("[SUCCESS] Updated Channel: id={}", channelId);

    ChannelDto channelDto =
        channelMapper.toDto(
            channel,
            channel.getParticipants(),
            lastMessageAt
        );

    eventPublisher.publishEvent(
        new ChannelUpdatedEvent(channelDto)
    );

    return channelDto;
  }

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  public void delete(UUID channelId) {
    Channel channel = findChannelEntityById(channelId);

    ChannelDto channelDto =
        channelMapper.toDto(
            channel,
            channel.getParticipants(),
            null
        );

    messageRepository.deleteByChannel(channel);
    readStatusRepository.deleteByChannel(channel);

    channelRepository.delete(channel);

    eventPublisher.publishEvent(
        new ChannelDeletedEvent(channelDto)
    );

    log.info("[SUCCESS] Deleted Channel: id={}", channelId);
  }

  // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
  private Channel findChannelEntityById(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException(id));
  }
}
