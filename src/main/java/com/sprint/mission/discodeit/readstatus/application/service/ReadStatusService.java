package com.sprint.mission.discodeit.readstatus.application.service;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.readstatus.application.mapper.ReadStatusDomainMapper;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusException;
import com.sprint.mission.discodeit.readstatus.domain.provider.ReadStatusChannelResolver;
import com.sprint.mission.discodeit.readstatus.domain.provider.ReadStatusUserResolver;
import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.user.domain.entity.User;
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

  private final ReadStatusRepository repository;
  private final ReadStatusUserResolver userProvider;
  private final ReadStatusChannelResolver channelProvider;
  private final ReadStatusDomainMapper mapper;

  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return repository.findAllByUser_Id(userId);
  }

  public ReadStatus create(ReadStatusCreateRequest request) {
    verifyCreatable(request.getChannelId(), request.getUserId());

    User user = userProvider.getProxyOrThrow(request.getUserId());
    Channel channel = channelProvider.getProxyOrThrow(request.getChannelId());
    ReadStatus status = mapper.toEntity(request, user, channel);
    repository.save(status);
    return status;
  }

  @Transactional(readOnly = true)
  public ReadStatus find(UUID id) {
    return findById(id);
  }

  public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
    ReadStatus status = findById(id);
    mapper.partialUpdate(request, status);
    return status;
  }

  public void delete(UUID id) {
    ReadStatus status = findById(id);
    repository.delete(status);
  }

  public void create(UUID channelId,
      List<UUID> participantIds,
      boolean notificationEnabled) {
    verifyCreatable(channelId, participantIds);

    Channel channel = channelProvider.getProxy(channelId);
    List<User> users = userProvider.getProxy(participantIds);
    List<ReadStatus> statuses = mapper.toEntityFrom(channel, users, notificationEnabled);
    repository.saveAll(statuses);
  }

  private ReadStatus findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById,
        value -> new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, value));
  }

  private void verifyCreatable(UUID channelId, UUID userId) {
    if (repository.existsByChannel_IdAndUser_Id(channelId, userId)) {
      throw new ReadStatusException(ReadStatusErrorCode.READSTATUS_ALREADY_EXIST,
          Map.of("userId", userId, "channelId", channelId));
    }
  }

  private void verifyCreatable(UUID channelId, List<UUID> userIds) {
    long count = repository.countByChannel_IdAndUser_IdIn(channelId, userIds);
    if (count > 0) {
      throw new ReadStatusException(ReadStatusErrorCode.READSTATUS_ALREADY_EXIST);
    }
  }
}

