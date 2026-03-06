package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(LoginRequest userLoginRequestDTO) {
        String username = userLoginRequestDTO.username();
        String password = userLoginRequestDTO.password();
        // username 일치하는 유저 있는지 확인 -> UserRepository에 메서드 정의
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(username+"의 유저는 찾지 못햇습니다"));
        // password 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        return userMapper.toDto(user);
    }
}
