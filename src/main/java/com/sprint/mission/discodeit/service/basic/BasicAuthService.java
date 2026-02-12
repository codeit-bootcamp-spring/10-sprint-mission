package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AutoService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicAuthService implements AutoService {

    private final EntityFinder entityFinder;

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto.UserResponse login(String userName, String password){
        Objects.requireNonNull(userName, "유저 이름을 입력해주세요.");
        Objects.requireNonNull(password, "비밀번호를 입력해주세요.");
        User member = userRepository.findAll().stream()
                .filter(user -> user.getName().equals(userName) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("로그인 실패"));
        UserStatus status = entityFinder.getStatusByUser(member.getId());
        if (status.isOnline()) throw new IllegalStateException("이미 접속중인 계정입니다.");
        status.userLogin();
        userStatusRepository.save(status);
        return UserDto.UserResponse.from(member, status);
    }

    @Override
    public UserDto.UserResponse logout(String userName) {
        Objects.requireNonNull(userName, "유저 이름을 입력해주세요.");
        User member = userRepository.findAll().stream()
                .filter(user -> user.getName().equals(userName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
        User user = entityFinder.getUser(member.getId());
        UserStatus status = entityFinder.getStatusByUser(member.getId());
        if (!status.isOnline())
            throw new IllegalArgumentException("해당 유저는 로그인 상태가 아닙니다.");
        status.userLogout();
        userStatusRepository.save(status);
        return UserDto.UserResponse.from(user, status);
    }
}
