package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UUID login(LoginRequestDto loginRequestDto) {
        List<User> userList = userRepository.findAll();
        User targetUser = userList.stream()
                .filter(user -> user.getUsername().equals(loginRequestDto.username()) &&
                        user.getPassword().equals(loginRequestDto.password()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Invalid username or password"));

        UserStatus userStatus = userStatusRepository.findByUserId(targetUser.getId())
                .get();
                userStatus.setLastOnlineTime(Instant.now());
        userStatusRepository.save(userStatus);

        return targetUser.getId();
    }
}
