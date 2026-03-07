package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authdto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserMapper;
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
            throw new NoSuchElementException("해당 유저는 존재하지 않음.");
        }

        // username이 존재할 때, password를 검증하고 예외를 던짐
        Optional<User> optUser = userRepository.findByUsernameAndPassword(req.username(),
            req.password());
        if (optUser.isEmpty()) {
            throw new IllegalStateException("로그인 정보가 옳바르지 않습니다!");
        }

        User user = optUser.get();

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NoSuchElementException("해당 User Status가 없습니다."));
        userStatus.update(Instant.now());
        return userMapper.toDto(user);

    }
}
