package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private UserRepository userRepository;

    public void login(UserRequestDto userLoginDto) {
        boolean existUserName = userRepository.existsByUsername(userLoginDto.userName());
        boolean existPassword = userRepository.existsByPassword(userLoginDto.password());
        if(existUserName && existPassword) {
            System.out.println("유저이름: " + userLoginDto.userName() + "\n유저이메일: " + userLoginDto.email());
        }
        else {
            throw new IllegalArgumentException("아이디및 비밀번호를 확인해주세요!");
        }

    }
}
