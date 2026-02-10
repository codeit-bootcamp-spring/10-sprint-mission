package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserLoginRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserStatusService userStatusService;
    private final UserStatusRepository userStatusRepository;

    public User login(UserLoginRequest request){
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userStatusService.updateByUserId(user.getId());
        userStatusRepository.findByUserId(user.getId())
                .ifPresent(user::setUserStatus);

         return user;
    }
}
