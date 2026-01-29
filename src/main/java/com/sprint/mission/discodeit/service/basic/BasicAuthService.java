package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserLoginDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    public UserResponseDto login(UserLoginDto dto){
        User user = userRepository.findByUsernameAndPassword(dto.username(), dto.password())
                .orElseThrow(()-> new IllegalArgumentException("잘못된 이름 또는 비밀번호"));
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(()->new NoSuchElementException("No User Status found, UserId: "+user.getId()));
        return userMapper.toDto(user, status);
    }
}
