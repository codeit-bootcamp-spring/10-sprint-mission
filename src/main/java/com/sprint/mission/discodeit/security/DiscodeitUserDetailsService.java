package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  // 로그인 아이디(username)로 조회할 때 사용
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsernameWithProfile(username)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username));

    return new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());
  }

  // JWT 필터에서 토큰의 Subject(UUID)를 기반으로 유저를 조회할 때 사용
  @Transactional(readOnly = true)
  public UserDetails loadUserById(UUID id) {
    User user = userRepository.findByIdWithProfile(id)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자 ID입니다: " + id));

    return new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());
  }
}
