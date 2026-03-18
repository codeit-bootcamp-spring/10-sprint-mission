package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto login(UserDto.UserLoginRequest loginReq) {
        User user = userRepository.findByUsername(loginReq.username())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getPassword(), loginReq.password())) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        return mapper.toDto(user);
    }
}
