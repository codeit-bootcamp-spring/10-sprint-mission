package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", dto.userId()));
    Channel channel = channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new ChannelNotFoundException().addDetail("channelId", dto.channelId()));
    if (readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId()).isPresent()) {
      throw new ReadStatusAlreadyExistException().addDetail("userId", dto.userId())
          .addDetail("channelId", dto.channelId());
    }
    ReadStatus readStatus = readStatusRepository.save(
        ReadStatus.create(user, channel, dto.lastReadAt()));
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new ReadStatusNotFoundException().addDetail("readStatusId", readStatusId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID id, ReadStatusUpdateRequest dto) {
    ReadStatus status = find(id);
    status.update(dto.lastReadAt());//마지막 시간 현재로 업데이트
    return readStatusMapper.toDto(status);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new ReadStatusNotFoundException().addDetail("readStatusId", readStatusId);
    }
    readStatusRepository.deleteById(readStatusId);
  }

  /* 이후 채널 검증 도입 시 사용
  private void checkValidation(UUID userId, UUID channelId) {
    if (userRepository.findById(userId).isEmpty()) {
      throw new UserNotFoundException().addDetail("userId", userId);
    }
    if (channelRepository.findById(channelId).isEmpty()) {
      throw new ChannelNotFoundException().addDetail("channelId", channelId);
    }
    if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
      throw new ReadStatusAlreadyExistException().addDetail("userId", userId)
          .addDetail("channelId", channelId);
    }
  }
   */
}
