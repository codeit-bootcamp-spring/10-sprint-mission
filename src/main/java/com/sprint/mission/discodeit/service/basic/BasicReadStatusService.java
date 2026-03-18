package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest readStatusCreateRequest) {
    // 유저 검색
    User user = userRepository.findById(readStatusCreateRequest.userId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 채널 검색
    Channel channel = channelRepository.findById(readStatusCreateRequest.channelId())
        .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

    // 유저가 해당 채널에 대한 메시지 읽음 상태가 이미 존재하면 400 예외 발생
    boolean exists = readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId());
    if (exists) {
      throw new BusinessException(ErrorCode.READ_STATUS_ALREADY_EXISTS);
    }

    Instant lastReadAt = readStatusCreateRequest.lastReadAt();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

    readStatusRepository.save(readStatus);

    return readStatusMapper.toDto(readStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
    Instant newLastReadAt = readStatusUpdateRequest.newLastReadAt();
    // 메시지 읽음 상태 조회
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND));

    readStatus.update(newLastReadAt);

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND));

    readStatusRepository.delete(readStatus);
  }
}
