package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserResponse login(LoginRequest request){
        request.validate();
        User user = userRepository.findAll().stream()
                .filter(u-> request.userName().equals(u.getUserName()))
                .filter(u-> request.password().equals(u.getPassword()))
                .findFirst()
                .orElseThrow(()-> new NoSuchElementException("비밀번호가 올바르지 않습니다."));

        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserName(),
                user.getAlias(),
                user.getEmail(),
                user.getProfileId(),
                true
        );
    }
}
