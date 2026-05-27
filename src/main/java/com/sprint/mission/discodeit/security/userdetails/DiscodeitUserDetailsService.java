package com.sprint.mission.discodeit.security.userdetails;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 로그인할 때 입력된 username으로 사용자를 조회하고,
// 조회한 사용자 정보를 인증에 필요한 UserDetails로 변환해 반환
@Service
@RequiredArgsConstructor
@Transactional
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 조회
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Principal에 담아둘 UserDto(사용자 정보)
        UserDto userDto = userMapper.toDto(user);

        // DiscodeitUserDetails에 UserDto와 encodedPassword(해시된 비밀번호) 함께 보관
        return new DiscodeitUserDetails(userDto, user.getPassword());
    }
}
