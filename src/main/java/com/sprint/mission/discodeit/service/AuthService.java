package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserLoginInfo;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.dto.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthService {
    UserRepository userRepository;
    public UserInfo login(UserLoginInfo userLoginInfo) {
        User findUser = userRepository.findByName(userLoginInfo.userName())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다."));
        if (!findUser.getPassword().equals(userLoginInfo.password()))
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        return UserMapper.toUserInfo(findUser);
    }
}
