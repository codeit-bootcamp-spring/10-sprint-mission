package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.UserOnlineStatusChecker;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    // 인증 로직이 도메인 규칙을 우회하지 않도록 UserService 의존
    private final UserService userService;
    private final UserMapper userMapper;

    private final UserOnlineStatusChecker checker;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        UserDto userDto = userMapper.toDto(user, checker.isOnline(user.getId()));

        return new DiscodeitUserDetails(
                userDto,
                user.getPassword()
        );
    }
}
