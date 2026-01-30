package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicLoginService implements LoginService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        // 이름과 일치하는 객체 찾기
        User user = userRepository.findByName(userLoginRequestDto.getName())
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다!"));
        // 이름이 일치하고 비밀번호가 일치하는 겍체 찾기
        if(!userLoginRequestDto.getPassword().equals(user.getPassword())){
            throw new NoSuchElementException("해당 유저가 없습니다!");
        }

        return userMapper.toDto(user,true);
    }
}
