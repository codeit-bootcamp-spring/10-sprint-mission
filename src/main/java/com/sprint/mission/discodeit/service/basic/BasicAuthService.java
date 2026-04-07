package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.PasswordEmptyException;
import com.sprint.mission.discodeit.exception.auth.WrongPasswordException;
import com.sprint.mission.discodeit.exception.common.BadRequestException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
      throw new BadRequestException();
    }

    if (request.password().isEmpty()) {
      throw new PasswordEmptyException();
    }

    User user = userRepository.findByUsername(request.userName());
    if (user == null) {
      throw new UserNotFoundException(request.userName());
    }

    if (!user.getPassword().equals(request.password())) {
      throw new WrongPasswordException();
    }

    return user;
  }
}