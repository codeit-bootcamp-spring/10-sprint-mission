package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDTO.Response login(UserDTO.Login request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.username()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저이름 입니다."));

        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));

        status.updateOnline();
        userStatusRepository.save(status);

        return UserDTO.Response.of(user, status);
    }
}
