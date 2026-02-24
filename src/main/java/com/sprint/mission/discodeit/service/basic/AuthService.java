package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.userSatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private UserRepository userRepository;

    public UserResponseDto login(UserRequestDto userLoginDto) {

        return userRepository.findByUserNameAndPassword(userLoginDto.userName(), userLoginDto.password())
                .map(user -> new UserResponseDto(user.getId(),user.getUserName(),user.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호 오류"));

    }
}
