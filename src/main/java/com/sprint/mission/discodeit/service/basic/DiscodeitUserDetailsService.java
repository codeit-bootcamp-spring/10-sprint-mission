package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final JwtRegistry jwtRegistry;

    // 로그인: Security가 DB로부터 사용자가 입력한 사용자 정보를 가져오는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = getUserEntityOrThrow(username);
        boolean isOnline = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());

        UserDto userDto = userMapper.toDto(user, isOnline);

        // 데이터베이스에 저장되어 있던 사용자의 정보 반환
        return new DiscodeitUserDetails(userDto, user.getPassword());
    }

    // 사용자 정보 조회
    public UserDetails loadUserById(String userId) {
        UserEntity user = getUserEntityOrThrow(UUID.fromString(userId));
        boolean isOnline = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());

        UserDto userDto = userMapper.toDto(user, isOnline);
        return new DiscodeitUserDetails(userDto, user.getPassword());
    }

    // 사용자 반환 (userId)
    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 사용자 반환 (username)
    private UserEntity getUserEntityOrThrow(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
