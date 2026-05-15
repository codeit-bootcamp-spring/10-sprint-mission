package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto login(LoginRequestDTO dto) {
        // username 존재 여부 확인까지 하기
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new UserNotFoundException(dto.username()));

        // BCrypt는 같은 비밀번호여도 매번 다른 해시값 생성
        // 따라서 matches를 사용해서 내부적으로 비교해야함
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("[LOGIN_FAIL] 로그인 실패: username={}", user.getUsername());
            throw new AuthException(
                    ErrorCode.LOGIN_FAIL,
                    Map.of("username", dto.username())
            );
        }

        log.info("[LOGIN_SUCCESS] 로그인 성공: username={}", user.getUsername());
        return authMapper.toDto(user);
    }

}
