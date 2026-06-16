package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent;
import com.sprint.mission.discodeit.event.sse.ChannelChangedEvent.Action;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ApplicationEventPublisher eventPublisher;

  @CacheEvict(value = "channels", allEntries = true)
  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    log.info("[CHANNEL] Public 채널 생성 완료: channelId={}", channel.getId());
    ChannelDto dto = channelMapper.toDto(channel);
    eventPublisher.publishEvent(new ChannelChangedEvent(dto, Action.CREATED));
    return dto;
  }

  @CacheEvict(value = "channels", allEntries = true)
  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new))
        .forEach(user -> readStatusRepository.save(
            new ReadStatus(user, channel, channel.getCreatedAt(), true)));
    log.debug("[CHANNEL] ReadStatus 생성 완료: readStatusCount={}",
        request.participantIds().size());
    log.info("[CHANNEL] Private 채널 생성 완료: channelId={}", channel.getId());
    ChannelDto dto = channelMapper.toDto(channel);
    eventPublisher.publishEvent(new ChannelChangedEvent(dto, Action.CREATED));
    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    log.debug("[CHANNEL] 채널 조회 완료: channelId={}, channelType={}", channel.getId(),
        channel.getType());
    return channelMapper.toDto(channel);
  }

  @Cacheable("channels")
  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAllByUserId(userId); //쿼리튜닝
    log.debug("[CHANNEL] 유저가 참여 중인 채널 목록 조회 완료: channelCount={}", channels.size());
    return channels.stream()
        .map(channelMapper::toDto) //이 메서드는 매핑 과정에서 MessageRepository와 ReadStatusRepository를 각각 호출
        .toList();
  }

  @CacheEvict(value = "channels", allEntries = true)
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }
    channel.update(request);
    log.info("[CHANNEL] Public 채널 수정 완료: channelId={}", channelId);
    ChannelDto dto = channelMapper.toDto(channel);
    eventPublisher.publishEvent(new ChannelChangedEvent(dto, Action.UPDATED));
    return dto;
  }

  @CacheEvict(value = "channels", allEntries = true)
  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    channelRepository.delete(channel);
    ChannelDto dto = channelMapper.toDto(channel);
    eventPublisher.publishEvent(new ChannelChangedEvent(dto, Action.DELETED));
    log.info("[CHANNEL] 채널 삭제 완료: channelId={}", channelId);
  }
}
