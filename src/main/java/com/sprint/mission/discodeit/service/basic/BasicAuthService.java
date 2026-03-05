package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final AuthMapper authMapper;

    @Override
    public UserDto login(LoginRequestDTO dto) {
        // username 존재 여부 확인까지 하기
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.username()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 이름으로 사용자가 존재하지 않습니다."
                ));

        if (!dto.password().equals(user.getPassword())) {
            throw new IllegalArgumentException("로그인에 실패하였습니다.");
        }

        return authMapper.toDto(user);
    }

}
