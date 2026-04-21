package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authDto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.others.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(LoginRequestDto dto) {
        // 이름과 일치하는 객체 찾기
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(()->new UserNotFoundException(dto.getUsername()));
        // 이름이 일치하고 비밀번호가 일치하는 겍체 찾기
        if(!dto.getPassword().equals(user.getPassword())){
            throw new WrongPasswordException();
        }

        return userMapper.toDto(user,true);
    }
}
