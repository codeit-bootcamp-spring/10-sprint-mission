package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 유저 id(이메일) 가져오기
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

    UserStatus status = userStatusRepository.findByUserId(user.getId());

    // 모든 사용자를 보기 위해서 null로 만듦
    BinaryContentDto profileDto = null;
    // 사용자가 프로필 이미지를 가지고 있으면 id를 가져오기
    if (user.getProfileImageId() != null) {
      profileDto = binaryContentRepository.findById(user.getProfileImageId())
          .map(binaryContentMapper::toDto)
          .orElse(null);
    }

    UserDto userDto = userMapper.toDto(user, status, profileDto);

    return new DiscodeitUserDetails(userDto, user.getPassword());
  }
}
