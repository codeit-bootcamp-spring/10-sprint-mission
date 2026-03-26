package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto login(LoginRequest request) {
    log.debug("유저 로그인 시작: username={}", request.username());
    User user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new DiscodeitException(ErrorCode.USER_NOT_FOUND));

    if (!user.getPassword().equals(request.password())) {
      throw new PasswordMismatchException(Map.of("username", request.username()));
    }
    log.info("유저 로그인 완료: username={}, email={}", user.getUsername(), user.getEmail());
    return userMapper.toDto(user);
  }
}
