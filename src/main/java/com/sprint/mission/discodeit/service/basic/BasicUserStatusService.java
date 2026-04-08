package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserStatusService extends BasicDomainService<UserStatus>
    implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto update(UUID userId, UserStatusUpdateRequest request) {
    UserStatus status = findById(userId);
    userStatusMapper.partialUpdate(request, status);
    return userStatusMapper.toDto(status);
  }

  @Override
  protected UserStatus findById(UUID userId) {
    return getOrThrow(userId, userStatusRepository::findByUserId,
        value -> new UserException(UserErrorCode.USERSTATUS_NOT_FOUND, value));
  }
}
