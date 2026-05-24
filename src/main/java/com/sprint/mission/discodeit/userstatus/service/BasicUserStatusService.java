package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;
  private final DomainServiceSupport domainTemplate;

  @Override
  public UserStatusDto update(UUID userId, UserStatusUpdateRequest request) {
    UserStatus status = findById(userId);
    userStatusMapper.partialUpdate(request, status);
    return userStatusMapper.toDto(status);
  }

  private UserStatus findById(UUID userId) {
    return domainTemplate.getOrThrow(userId, userStatusRepository::findByUserId,
        value -> new UserException(UserErrorCode.USERSTATUS_NOT_FOUND, value));
  }
}
