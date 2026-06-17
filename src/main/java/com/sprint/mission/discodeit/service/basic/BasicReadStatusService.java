package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidParameterException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public UUID create(ReadStatusCreateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userId(), "userId");
    requireNonNull(request.channelId(), "channelId");

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

    ReadStatus duplicated = readStatusRepository.findByUserIdAndChannelId(
        request.userId(), request.channelId()
    );

    if (duplicated != null) {
      throw new ReadStatusAlreadyExistsException();
    }

    boolean notificationEnabled = request.notificationEnabled() != null
        ? request.notificationEnabled()
        : channel.getType() == ChannelType.PRIVATE;

    ReadStatus readStatus = new ReadStatus(user, channel, notificationEnabled);

    Instant lastReadAt = (request.lastReadAt() == null) ? Instant.now() : request.lastReadAt();
    readStatus.updateLastReadAt(lastReadAt);

    return readStatusRepository.save(readStatus).getId();
  }

  @Override
  public ReadStatusResponse find(UUID id) {
    requireNonNull(id, "id");

    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new ReadStatusNotFoundException(id));

    return readStatusMapper.toResponse(readStatus);
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toResponse)
        .toList();
  }

  @Override
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest req) {
    requireNonNull(readStatusId, "readStatusId");
    requireNonNull(req, "request");

    ReadStatus rs = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

    if (req.newLastReadAt() != null) {
      rs.updateLastReadAt(req.newLastReadAt());
    }

    if (req.newNotificationEnabled() != null) {
      rs.updateNotificationEnabled(req.newNotificationEnabled());
    }

    readStatusRepository.save(rs);

    return readStatusMapper.toResponse(rs);
  }

  @Override
  public void delete(UUID id) {
    requireNonNull(id, "id");

    ReadStatus existing = readStatusRepository.findById(id)
        .orElseThrow(() -> new ReadStatusNotFoundException(id));

    readStatusRepository.delete(existing.getId());
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new InvalidParameterException(name);
    }
  }
}