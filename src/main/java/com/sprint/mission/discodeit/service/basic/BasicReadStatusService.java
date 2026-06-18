package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    ReadStatus existing = readStatusRepository
        .findByChannelIdAndUserId(request.getChannelId(), request.getUserId())
        .orElse(null);

    if (existing != null) {
      existing.updateLastReadAt();
      return readStatusMapper.toDto(existing);
    }
    User user = findUserEntityById(request.getUserId());
    Channel channel = findChannelEntityById(request.getChannelId());

    boolean notificationEnabled = channel.getType() == ChannelType.PRIVATE;

    ReadStatus readStatus =
        new ReadStatus(
            user,
            channel,
            notificationEnabled
        );

    return readStatusMapper.toDto(readStatusRepository.save(readStatus));

  }

  @Override
  public ReadStatusDto findById(UUID id) {
    return readStatusMapper.toDto(findReadStatusEntityById(id));
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = findReadStatusEntityById(readStatusId);
    readStatus.updateLastReadAt();

    if (request.getNotificationEnabled() != null) {
      readStatus.updateNotificationEnabled(
          request.getNotificationEnabled()
      );
    }
    
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    ReadStatus readStatus = findReadStatusEntityById(id);
    readStatusRepository.delete(readStatus);
  }

  // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
  private ReadStatus findReadStatusEntityById(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND));
  }

  // [헬퍼 메서드]: 유저 ID 조회
  private User findUserEntityById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  // [헬퍼 메서드]: 채널 ID 조회
  private Channel findChannelEntityById(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException(id));
  }
}
