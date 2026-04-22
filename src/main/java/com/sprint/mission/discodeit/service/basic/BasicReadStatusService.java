package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusErrorCode;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;
  private final BasicDomainTemplate domainTemplate;

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusMapper.toDto(readStatusRepository.findAllByUserId(userId));
  }

  public ReadStatusDto create(ReadStatusCreateRequest request) {
    verifyCreatable(request);
    User user = userRepository.getReferenceById(request.getUserId());
    Channel channel = channelRepository.getReferenceById(request.getChannelId());
    ReadStatus status = new ReadStatus(user, channel, request.getLastReadAt());
    readStatusRepository.save(status);
    return readStatusMapper.toDto(status);
  }

  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID id) {
    return readStatusMapper.toDto(findById(id));
  }

  public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
    ReadStatus status = findById(id);
    readStatusMapper.partialUpdate(request, status);
    return readStatusMapper.toDto(status);
  }

  @Override
  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, readStatusRepository,
        value -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, value));
  }

  private ReadStatus findById(UUID id) {
    return domainTemplate.getOrThrow(id, readStatusRepository::findById,
        value -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, value));
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID userId, UUID channelId) {
    ReadStatus status = domainTemplate.getOrThrow(
        Map.of("userId", userId, "channelId", channelId),
        map -> readStatusRepository.findByUserIdAndChannelId(map.get("userId"),
            map.get("channelId")),
        map -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, map));
    return readStatusMapper.toDto(status);
  }

  private void verifyCreatable(ReadStatusCreateRequest request) {
    UUID userId = request.getUserId();
    UUID channelId = request.getChannelId();
    domainTemplate.throwOrNot(userId, userRepository::existsById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
    domainTemplate.throwOrNot(channelId, channelRepository::existsById,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
    domainTemplate.throwOrNot(Map.of("userId", userId, "channelId", channelId),
        map -> !readStatusRepository.existsByUserIdAndChannelId(map.get("userId"),
            map.get("channelId")),
        map -> new ReadStatusException(ReadStatusErrorCode.READSTATUS_ALREADY_EXIST, map));
  }
}
