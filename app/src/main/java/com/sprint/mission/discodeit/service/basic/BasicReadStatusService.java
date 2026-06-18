package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", request.userId())));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", request.channelId())));

    readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .ifPresent(status -> {
          throw new ReadStatusAlreadyExistsException(Map.of(
              "userId", request.userId(), "channelId", request.channelId()));
        });
    boolean enabled = channel.getType() == ChannelType.PRIVATE;
    ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt(), enabled);
    readStatusRepository.save(readStatus);
    log.info("[READ_STATUS] ReadStatus 생성 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto findById(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    log.debug("[READ_STATUS] ReadStatus 조회 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
    log.debug("[READ_STATUS] 유저의 ReadStatus 목록 조회 완료: readStatusCount={}", readStatuses.size());
    return readStatuses.stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());
    log.info("[READ_STATUS] ReadStatus 수정 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    readStatusRepository.delete(readStatus);
    log.info("[READ_STATUS] ReadStatus 삭제 완료: readStatusId={}", readStatusId);
  }

  @Transactional(readOnly = true)
  public boolean isReadStatusUser(UUID readStatusId, UUID userId) {
    return readStatusRepository.findById(readStatusId)
        .map(ReadStatus::getUser)
        .map(user -> user.getId().equals(userId))
        .orElse(false);
  }
}
