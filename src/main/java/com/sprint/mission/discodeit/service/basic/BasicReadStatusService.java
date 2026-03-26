package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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
    log.debug("ReadStatus 생성 시작: userId={}, channelId={}, lastReadAt={}", request.userId(),
        request.channelId(), request.lastReadAt());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", request.userId())));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", request.channelId())));

    readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .ifPresent(status -> {
          throw new ReadStatusAlreadyExistsException(Map.of(
              "userId", request.userId(), "channelId", request.channelId()));
        });

    ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
    readStatusRepository.save(readStatus);
    log.info("ReadStatus 생성 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto findById(UUID readStatusId) {
    log.debug("ReadStatus 조회 시작: readStatusId={}", readStatusId);
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    log.debug("ReadStatus 조회 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("유저의 ReadStatus 목록 조회 시작: userId={}", userId);
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
    log.debug("유저의 ReadStatus 목록 조회 완료: readStatusCount={}", readStatuses.size());
    return readStatuses.stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.debug("ReadStatus 수정 시작: readStatusId={}, newLastReadAt={}", readStatusId,
        request.newLastReadAt());
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    readStatus.updateLastReadAt(request.newLastReadAt());
    log.info("ReadStatus 수정 완료: readStatusId={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    log.debug("ReadStatus 삭제 시작: readStatusId={}", readStatusId);
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)));
    readStatusRepository.delete(readStatus);
    log.info("ReadStatus 삭제 완료: readStatusId={}", readStatusId);
  }
}
