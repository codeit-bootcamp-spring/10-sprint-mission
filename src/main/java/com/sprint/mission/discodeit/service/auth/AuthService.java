package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.auth.response.AuthServiceResponse;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.auth.AuthMapper;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final AuthMapper authMapper;

    public AuthServiceResponse login(AuthServiceRequest request){
        // name 같은 지 확인
        User user = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(request.username()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.username()));

        // password 같은지 확인
        if(!(user.getPassword().equals(request.password()))){
            throw new IllegalArgumentException("Wrong password");
        }

        UserStatus status = userStatusRepository.findByUserID(user.getId());

        // 유저 정보 반환, DTO로 ??
        return authMapper.toResponse(user, status);
    }
}
