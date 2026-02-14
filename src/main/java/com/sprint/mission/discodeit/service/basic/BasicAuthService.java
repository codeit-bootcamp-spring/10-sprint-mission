package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    // 로그인
    public User login(AuthLoginRequestDTO authLoginRequestDTO) {
        User targetUser = userRepository.findById(authLoginRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        if (!targetUser.getPassword().equals(authLoginRequestDTO.getPassword()) ||
                !targetUser.getNickname().equals(authLoginRequestDTO.getNickname())){
            throw new IllegalArgumentException(("[로그인 실패] 잘못된 닉네임 혹은 비밀번호를 입력하셨습니다."));
        }

        return targetUser;
    }
}
