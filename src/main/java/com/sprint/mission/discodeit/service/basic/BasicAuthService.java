package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserResponse login(LoginRequest request) {
    User user = userRepository.findAll().stream()
        .filter(u -> u.getUsername().equals(request.username()))
        .findFirst()
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    if (!user.getPassword().equals(request.password())) {
      throw new BusinessLogicException(ExceptionCode.PASSWORD_MISMATCH);
    }

    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));

    return UserResponse.of(user, userStatus);
  }
}
