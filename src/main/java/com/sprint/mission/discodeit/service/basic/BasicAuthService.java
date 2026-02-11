package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CanNotLoginException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null ||
                request.userName() == null ||
                request.password() == null ||
                request.password().isEmpty()) {
            throw new CanNotLoginException();
        }

        User user = userRepository.findAll().stream()
                .filter(u ->
                        u.getName().equals(request.userName()) &&
                                u.getPassword().equals(request.password())
                )
                .findFirst()
                .orElseThrow(CanNotLoginException::new);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                Optional.ofNullable(user.getProfileImageId())
        );
    }
}
