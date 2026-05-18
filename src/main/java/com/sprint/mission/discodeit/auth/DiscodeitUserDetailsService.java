package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

//username으로 UserDetails를 조회하는 서비스
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //discodeit은 로그인시 사용자이름이 ID에 해당됨.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /// profile이 LAZY로 돼있어서 fetch join으로 profile 바로 가져오도록 했다.
        User findUser = userRepository.findByUsernameWithProfileAndStatus(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));

        UserDto userDto = userMapper.toDto(findUser);

        /**
         로그인시, 매번 새 객체를 만든다.
         즉, 같은 계정으로 로그인해도 JVM 객체는 매번 다르다.
         **/
        return new DiscodeitUserDetails(userDto, findUser.getPassword());
    }
}
