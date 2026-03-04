package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserDto.userResponse login(UserDto.userLoginRequest loginReq) {
        User user = userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getUsername(), loginReq.username()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getPassword(), loginReq.password())) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        return new UserDto.userResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(),
                user.getProfileId(), true);
    }
}
