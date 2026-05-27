package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 이때 Custom 예외 반환 or Security 예외 반환??, Username 중복 허용일 떄는 이때 어떻게 해야하지
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        UserDTO userDTO = userMapper.toDTO(user);
        return new DiscodeitUserDetails(userDTO, user.getPassword());
    }
}
