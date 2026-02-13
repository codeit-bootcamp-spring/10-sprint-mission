package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.UserLoginInfo;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.AuthenticationFailedException;
import com.sprint.mission.discodeit.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.userstatus.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    public UserInfo login(UserLoginInfo userLoginInfo) {
        User findUser = userRepository.findByName(userLoginInfo.userName())
                .orElseThrow(UserNotFoundException::new);
        if (!findUser.getPassword().equals(userLoginInfo.password()))
            throw new AuthenticationFailedException();
        UserStatus status = userStatusRepository.findByUserId(findUser.getId())
                .orElseThrow(UserStatusNotFoundException::new);;
        status.updateLastOnlineAt();
        userStatusRepository.save(status);
        return UserMapper.toUserInfo(findUser, status);
    }
}
