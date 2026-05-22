package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

    BinaryContentDto profileDto = null;
    if (user.getProfileImageId() != null) {
      profileDto = binaryContentRepository.findById(user.getProfileImageId())
          .map(binaryContentMapper::toDto)
          .orElse(null);
    }

    UserDto userDto = userMapper.toDto(user, true, profileDto);

    return new DiscodeitUserDetails(userDto, user.getPassword());
  }
}