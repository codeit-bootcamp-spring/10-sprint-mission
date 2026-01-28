package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserDTO.UserLoginDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(UserLoginDTO req) {
        return userRepository.findAll()
                .stream()
                .filter(u -> req.username().equals(u.getUsername()) && req.password().equals(u.getPassword()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("아이디/비밀번호가 올바르지 않습니다."));
    }
}
