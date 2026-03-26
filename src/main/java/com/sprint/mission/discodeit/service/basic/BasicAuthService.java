package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final AuthMapper authMapper;

    @Override
    public UserDto login(LoginRequestDTO dto) {
        // username 존재 여부 확인까지 하기
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.username()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(dto.username()));

        if (!dto.password().equals(user.getPassword())) {
            log.warn("[LOGIN_FAIL] 로그인 실패: username={}", user.getUsername());
            throw new IllegalArgumentException("로그인에 실패하였습니다.");
        }

        log.info("[LOGIN_SUCCESS] 로그인 성공: username={}", user.getUsername());
        return authMapper.toDto(user);
    }

}
