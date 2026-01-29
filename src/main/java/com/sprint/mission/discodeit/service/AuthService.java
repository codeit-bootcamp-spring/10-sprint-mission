package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public User login(AuthServiceRequest request){
        // name 같은 지 확인
        User user = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(request.username()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.username()));

        // password 같은지 확인
        if(!(user.getPassword().equals(request.password()))){
            throw new IllegalArgumentException("Wrong password");
        }

        // 유저 정보 반환, 얘도 DTO로 ??
        return user;
    }
}
