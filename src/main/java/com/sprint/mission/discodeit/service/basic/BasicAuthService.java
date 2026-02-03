package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
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
    public UserDto.response login(UserDto.LoginRequest loginReq) {
        User user = userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getAccountId(), loginReq.accountId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));

        if (!Objects.equals(user.getPassword(), loginReq.password())) {
            throw new IllegalStateException("비밀번호가 틀렸습니다");
        }

        return new UserDto.response(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getAccountId(), user.getName(), user.getMail(),
                user.getProfileId(), true,
                user.getJoinedChannels().stream().toList(),
                user.getMessageHistory());
    }
}
