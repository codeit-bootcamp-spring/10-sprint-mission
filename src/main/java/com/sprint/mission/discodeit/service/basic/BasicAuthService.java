package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserLoginDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(UserLoginDto dto){
        User user = userRepository.findByUsernameAndPassword(dto.username(), dto.password())
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS));
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
        return userMapper.toDto(user, status);
    }
}
