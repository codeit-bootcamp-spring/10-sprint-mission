package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    public UserDto login(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.username())
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NAME_NOT_FOUND,
                    loginDto.username())
            );

        if (!user.getPassword().equals(loginDto.password())) {
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);
        }

        // 해당 user의 userStatus가 존재한다면 lastAccessedTime을 업데이트, 없다면 새로 만들어 저장한다.
        userStatusRepository.findByUserId(user.getId()).ifPresentOrElse(
            userStatus -> userStatus.updateLastAccessedTime(Instant.now()),
            () -> userStatusRepository.save(new UserStatus(user))
        );

        return userMapper.toDto(user);
    }
}
