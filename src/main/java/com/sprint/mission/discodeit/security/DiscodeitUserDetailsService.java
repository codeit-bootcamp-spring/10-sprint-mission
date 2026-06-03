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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        UserDTO userDTO = userMapper.toDTO(user);
        return new DiscodeitUserDetails(userDTO, user.getPassword());
    }

    public UserDetails loadUserById(UUID userId) {
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId.toString()));

        UserDTO userDTO = userMapper.toDTO(user);
        return new DiscodeitUserDetails(userDTO, user.getPassword());
    }
}
