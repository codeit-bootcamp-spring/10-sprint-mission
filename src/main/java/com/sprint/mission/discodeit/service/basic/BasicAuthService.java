package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authdto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto login(LoginRequestDTO req) {
        // req에 담긴 username을 조회 및 존재여부 파악.
        if (userRepository.findByUsername(req.username()).isEmpty()) {
            throw new UserNotFoundException(req.username());
        }

        // username이 존재할 때, password를 검증하고 예외를 던짐
        Optional<User> optUser = userRepository.findByUsernameAndPassword(req.username(),
            req.password());
        if (optUser.isEmpty()) {
            throw new UserNotFoundException(req.username());
        }

        User user = optUser.get();

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
            .orElseThrow(() -> new UserStatusNotFoundException(user.getId()));
        userStatus.update(Instant.now());
        return userMapper.toDto(user);

    }
}
