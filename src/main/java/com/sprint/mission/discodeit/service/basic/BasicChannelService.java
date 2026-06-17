package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    channelRepository.save(channel);
    log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
    return channelMapper.toDto(channel);
  }

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt(), true))
        .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
  }

  // @Cacheable : 캐시에 저장하고, 있으면 꺼내 쓰기
  // 메서드 호출 시, 먼저 "userChannels" 캐시에 해당 key의 값이 있는 지 확인
  //   cache hit : 메서드 본문을 실행하지 않고 캐시에 저장된 값을 바로 반환 (DB 조회 안함)
  //   cache miss : 메서드를 실행하고, 그 반환값을 캐시에 저장한 뒤 반환
  // key를 생략하면 메서드 파라미터로 자동 생성된 key를 사용 (파라미터가 없으면 SimpleKey.EMPTY)
  // #userId 지정하면 사용자별로 캐시 항목이 따로 저장된다.
  @Cacheable(value = "userChannels", key = "#userId")
  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 수정 시작: id={}, request={}", channelId, request);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw PrivateChannelUpdateException.forChannel(channelId);
    }
    channel.update(newName, newDescription);
    log.info("채널 수정 완료: id={}, name={}", channelId, channel.getName());
    return channelMapper.toDto(channel);
  }

  // CacheEvict - 캐시 비우기
  // 메서드 실행 후 해당 캐시의 항목을 제거. 캐시의 stale data를 없애는 용도이다.
  // allEntries = true : userChannels의 캐시 전체를 비운다.
  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시작: id={}", channelId);
    if (!channelRepository.existsById(channelId)) {
      throw ChannelNotFoundException.withId(channelId);
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);

    channelRepository.deleteById(channelId);
    log.info("채널 삭제 완료: id={}", channelId);
  }
}

/*
읽는 메서드에는 @Cacheable
쓰는 메서드에는 @CacheEvict를 붙여서
조회는 빠르게, 데이터가 바뀌면 캐시를 갱신하는 구조
 */