package com.sprint.mission.discodeit.service.cache;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Cacheable(value = "userListCache", key = "'without_session'")
  public List<UserDto> findAllUsers() {
    log.debug("사용자 목록 조회(새로운 유저 목록 캐시 갱신)");
    return userRepository.findAllFetchUserInfo().stream()
        .map(u -> userMapper.toDto(u, false))
        .toList();
  }
}
