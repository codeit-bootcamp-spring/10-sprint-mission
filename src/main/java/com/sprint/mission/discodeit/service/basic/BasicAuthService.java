package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserLoginDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto login(UserLoginDto request) {
        User user = userRepository.findByName(request.userName())
                .filter(u -> u.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("해당 정보와 일치하는 사용자가 없습니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseGet(() -> new UserStatus(user.getId()));
        userStatus.updateLastActiveTime();
        userStatus.updateStatusType();
        userStatusRepository.save(userStatus);
        return userMapper.toUserInfoDto(user, userStatus.updateStatusType());
    }
}