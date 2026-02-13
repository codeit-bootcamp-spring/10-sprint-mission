package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authdto.AuthDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserDTOMapper;
import com.sprint.mission.discodeit.entity.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserDTOMapper userDTOMapper;

    @Override
    public UserResponseDTO login(AuthDTO req) {
        User user = userRepository.findAll()
                .stream()
                .filter(u -> req.email().equals(u.getEmail()) && req.password().equals(u.getPassword()))
                .findFirst().orElseThrow(() -> new NoSuchElementException("그런 유저는 없습니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("해당 User Status가 없습니다."));
        userStatus.update(Instant.now());
        userStatusRepository.save(userStatus);
        return userDTOMapper.userToResponse(user, userStatus);

    }
}
