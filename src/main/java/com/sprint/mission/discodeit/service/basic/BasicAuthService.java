package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper mapper;

  @Override
  public UserDto login(UserDto.UserLoginRequest loginReq) {
    log.debug("[Service] 로그인 요청 시작: username={}", loginReq.username());

    User user = userRepository.findByUsername(loginReq.username())
        .orElseThrow(() -> new UserNotFoundException());

    if (!Objects.equals(user.getPassword(), loginReq.password())) {
      throw new UserNotFoundException();
    }

    log.info("[Service] 로그인 요청 성공: id={}", user.getId());

    return mapper.toDto(user);
  }
}
