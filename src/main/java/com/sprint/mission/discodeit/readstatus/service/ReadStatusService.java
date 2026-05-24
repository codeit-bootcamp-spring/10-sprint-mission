package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.exception.ChannelException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.exception.ReadStatusException;
import com.sprint.mission.discodeit.readstatus.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;
  private final DomainServiceSupport domainTemplate;

  @Transactional(readOnly = true)
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    return readStatusMapper.toDto(readStatusRepository.findAllByUserId(userId));
  }

  public ReadStatusResponse create(ReadStatusCreateRequest request) {
    verifyCreatable(request);
    User user = userRepository.getReferenceById(request.getUserId());
    Channel channel = channelRepository.getReferenceById(request.getChannelId());
    ReadStatus status = new ReadStatus(user, channel, request.getLastReadAt());
    readStatusRepository.save(status);
    return readStatusMapper.toDto(status);
  }

  @Transactional(readOnly = true)
  public ReadStatusResponse find(UUID id) {
    return readStatusMapper.toDto(findById(id));
  }

  public ReadStatusResponse update(UUID id, ReadStatusUpdateRequest request) {
    ReadStatus status = findById(id);
    readStatusMapper.partialUpdate(request, status);
    return readStatusMapper.toDto(status);
  }

  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, readStatusRepository,
        value -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, value));
  }

  private ReadStatus findById(UUID id) {
    return domainTemplate.getOrThrow(id, readStatusRepository::findById,
        value -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, value));
  }

  @Transactional(readOnly = true)
  public ReadStatusResponse find(UUID userId, UUID channelId) {
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
