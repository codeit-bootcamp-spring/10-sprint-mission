package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.AuthErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto login(LoginRequest request) {
    User user = userRepository.findByUsernameAndPassword(request.getUsername(),
            request.getPassword())
        .orElseThrow(() -> new AuthException(AuthErrorCode.USERNAME_OR_PASSWORD_INCORRECT));
    return userMapper.toDto(user);
  }
}
