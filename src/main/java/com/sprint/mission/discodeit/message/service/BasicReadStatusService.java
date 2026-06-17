package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.message.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.message.dto.ReadStatusDto;
import com.sprint.mission.discodeit.message.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.message.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.channel.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final JPAUserRepository jpaUserRepository;
  private final JPAChannelRepository jpaChannelRepository;
  private final JPAReadStatusRepository jpaReadStatusRepository;
  private final ReadStatusMapper readStatusMapper;


  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    if (jpaReadStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
      throw new IllegalArgumentException("이미 해당 채널에 대한 읽기 상태가 존재합니다.");
    }

    User user = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    Channel channel = jpaChannelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found"));

    boolean notificationEnabled = (channel.getType() == ChannelType.PRIVATE);

    ReadStatus readStatus = jpaReadStatusRepository.save(
        new ReadStatus(user, channel, request.lastReadAt(), notificationEnabled)
    );
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID id) {
    return jpaReadStatusRepository.findById(id)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("해당 읽음 객체가 존재하지 않습니다"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return jpaReadStatusRepository.findAllByUserId(userId)
        .stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = jpaReadStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("해당 읽음 객체가 존재하지 않습니다"));
    if (request.newLastReadAt() != null) {
      readStatus.updateLastRead(request.newLastReadAt());
    }
    if (request.newNotificationEnabled() != null) {
      readStatus.updateNotificationEnabled(request.newNotificationEnabled());
    }
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    ReadStatus readStatus = jpaReadStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 읽음 객체를 찾을 수 없습니다."));
    jpaReadStatusRepository.delete(readStatus);
  }
}
