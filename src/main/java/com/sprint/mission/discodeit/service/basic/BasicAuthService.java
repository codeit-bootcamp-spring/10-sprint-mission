package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService {
    private final UserRepository userRepository;

    public User login(AuthLoginRequestDTO authLoginRequestDTO) {
        // 1. 사용자 존재 여부 확인
        User targetUser = userRepository.findById(authLoginRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 2. 일치 여부 확인
        if (!targetUser.getPassword().equals(authLoginRequestDTO.getPassword()) ||
                !targetUser.getNickname().equals(authLoginRequestDTO.getNickname())){
            throw new IllegalArgumentException(("[로그인 실패] 잘못된 닉네임 혹은 비밀번호를 입력하셨습니다."));
        }

        // 3. 일치하는 사용자 반환
        return targetUser;
    }
}
