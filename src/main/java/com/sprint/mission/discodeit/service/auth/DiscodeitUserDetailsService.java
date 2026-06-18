package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {

    return userRepository.findWithProfileByUsername(username)
        .map(user -> {

          UserDto userDto =
              userMapper.toDto(user, jwtRegistry);

          return new DiscodeitUserDetails(
              userDto,
              user.getPassword()
          );
        })
        .orElseThrow(() ->
            new UsernameNotFoundException(
                "사용자를 찾을 수 없습니다: " + username
            )
        );
  }
}
