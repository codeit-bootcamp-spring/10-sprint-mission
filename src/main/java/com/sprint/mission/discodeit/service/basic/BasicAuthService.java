package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService {

    private final UserRepository userRepository;
//    private final UserStatusRepository userStatusRepository;

    public UserResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(request.getUsername()))
                .findFirst();

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        User user = userOptional.get();

        if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
//        boolean isOnline = userStatusRepository.findByUserId(user.getId())
//                .map(UserStatus::isOnline)
//                .orElse(false);

        boolean isOnline = false;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                isOnline,
                user.getProfileId()
        );
    }
}