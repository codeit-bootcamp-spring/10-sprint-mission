package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper mapper;

  @Transactional
  @Override
  public ReadStatusDto createReadStatus(ReadStatusDto.ReadStatusCreateRequest createReq) {
    log.debug("[Service] ReadStatus 생성 시작: userId={}, channelId={}, lastReadAt={}",
        createReq.userId(), createReq.channelId(), createReq.lastReadAt());
    User user = userRepository.findById(createReq.userId())
        .orElseThrow(() -> new UserNotFoundException());
    Channel channel = channelRepository.findById(createReq.channelId())
        .orElseThrow(() -> new ChannelNotFoundException());

    readStatusRepository.findAllByUserId(user.getId()).stream()
        .filter(r -> Objects.equals(r.getChannel().getId(), channel.getId()))
        .findFirst()
        .ifPresent(r -> {
          throw new ReadStatusAlreadyExistsException();
        });

    ReadStatus readStatus = new ReadStatus(user, channel);
    readStatusRepository.save(readStatus);
    log.debug("[Service] ReadStatus 저장 완료: id={}", readStatus.getId());

    log.info("[Service] ReadStatus 생성 성공: id={}", readStatus.getId());
    return toResponse(readStatus);
  }

  @Override
  public ReadStatusDto findById(UUID uuid) {
    ReadStatus readStatus = readStatusRepository.findById(uuid)
        .orElseThrow(() -> new ReadStatusNotFoundException());

    return toResponse(readStatus);
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException();
    }

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto updateReadStatus(UUID uuid,
      ReadStatusDto.ReadStatusUpdateRequest updateReq) {
    log.debug("[Service] ReadStatus 수정 시작: id={}, newLastReadAt={}, newNotificationEnabled={}",
        uuid, updateReq.newLastReadAt(), updateReq.newNotificationEnabled());
    ReadStatus readStatus = readStatusRepository.findById(uuid)
        .orElseThrow(() -> new ReadStatusNotFoundException());

    Optional.ofNullable(updateReq.newLastReadAt())
        .ifPresent(readStatus::updateLastReadAt);
    Optional.ofNullable(updateReq.newNotificationEnabled())
        .ifPresent(readStatus::updateNotificationEnabled);
    readStatusRepository.save(readStatus);
    log.debug("[Service] 수정된 ReadStatus 저장 완료: id={}", readStatus.getId());

    log.info("[Service] ReadStatus 수정 성공: id={}", readStatus.getId());
    return toResponse(readStatus);
  }

  @Transactional
  @Override
  public void deleteReadStatusById(UUID uuid) {
    log.debug("[Service] ReadStatus 삭제 시작: id={}", uuid);

    readStatusRepository.deleteById(uuid);
    log.info("[Service] ReadStatus 삭제 성공: id={}", uuid);
  }

  private ReadStatusDto toResponse(ReadStatus readStatus) {
    return mapper.toDto(readStatus);
  }
}

