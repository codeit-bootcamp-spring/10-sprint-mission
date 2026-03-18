package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public User login(LoginRequest request) {
    if (request == null || request.userName() == null || request.password() == null) {
      throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
    }

    if (request.password().isEmpty()) {
      throw new BusinessLogicException(ErrorCode.WRONG_PASSWORD);
    }

    User user = userRepository.findByUsername(request.userName());
    if (user == null) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    if (!user.getPassword().equals(request.password())) {
      throw new BusinessLogicException(ErrorCode.WRONG_PASSWORD);
    }

    return user;
  }
}