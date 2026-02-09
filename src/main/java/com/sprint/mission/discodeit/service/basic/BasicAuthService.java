package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.auth.AuthResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public AuthResponseDto login(AuthLoginRequestDto request) {
        String username = request.userName();
        String password = request.password();
        User user = userRepository.findAll().stream()
                .filter(u -> username.equals(u.getUserName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!password.equals(user.getUserPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return  toDto(user);
    }

    public static AuthResponseDto toDto(User user) {
        return new AuthResponseDto(
                user.getId(),
                user.getUserName(),
                "로그인에 성공하였습니다."
        );
    }
}
