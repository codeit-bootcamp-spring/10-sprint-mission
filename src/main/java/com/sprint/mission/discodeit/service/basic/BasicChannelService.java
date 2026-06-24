package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.message.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ApplicationEventPublisher eventPublisher;

  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final CacheManager cacheManager;

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    channelRepository.save(channel);
    ChannelDto channelDto = channelMapper.toDto(channel);

    eventPublisher.publishEvent(new ChannelCreatedEvent(channelDto, Instant.now()));
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses =
        userRepository.findAllById(request.participantIds()).stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
    readStatusRepository.saveAll(readStatuses);
    evictCache(request.participantIds());

    ChannelDto channelDto = channelMapper.toDto(channel);

    eventPublisher.publishEvent(new ChannelCreatedEvent(channelDto, Instant.now()));

    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository
        .findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
  }

  @Cacheable(value = "channels", key = "#userId", unless = "#result.isEmpty()")
  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds =
        readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

    return channelRepository
        .findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 수정 시작: id={}, request={}", channelId, request);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel =
        channelRepository
            .findById(channelId)
            .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw PrivateChannelUpdateException.forChannel(channelId);
    }
    ChannelDto before = channelMapper.toDto(channel);
    channel.update(newName, newDescription);
    ChannelDto after = channelMapper.toDto(channel);

    eventPublisher.publishEvent(new ChannelUpdatedEvent(before, after, Instant.now()));
    return channelMapper.toDto(channel);
  }

  @CacheEvict(value = "channels", allEntries = true)
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시작: id={}", channelId);
    Channel channel =
        channelRepository
            .findById(channelId)
            .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

    ChannelDto deletedChannel = channelMapper.toDto(channel);

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);

    channelRepository.deleteById(channelId);

    eventPublisher.publishEvent(new ChannelDeletedEvent(deletedChannel, Instant.now()));
  }

  private void evictCache(List<UUID> userIds) {
    Cache cache = cacheManager.getCache("channels");
    if (cache != null) {
      for (UUID userId : userIds) {
        cache.evict(userId);
      }
      log.debug("채널 캐시를 제거했습니다: userIds={}", userIds);
    } else {
      log.warn("채널 캐시가 존재하지 않습니다.");
    }
  }
}
