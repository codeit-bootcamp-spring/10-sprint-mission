package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.user.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final DomainServiceSupport domainTemplate;

  public UserResponse updateRole(RoleUpdateRequest request) {
    User user = findById(request.getUserId());
    userMapper.partialUpdate(request, user);
    return userMapper.toDto(user);
  }

  private User findById(UUID id) {
    return domainTemplate.getOrThrow(id, userRepository::findById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }
}
