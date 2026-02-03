package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BasicAutoService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserDto.UserResponse login(String username, String password){
        Objects.requireNonNull(username, "유저 이름을 입력해주세요.");
        Objects.requireNonNull(password, "비밀번호를 입력해주세요.");
        User member = userRepository.findAll().stream()
                .filter(user -> user.getName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("로그인 실패"));
        UserStatus status = userStatusRepository.findByUserId(member.getId());

        return UserDto.UserResponse.from(member, status);
    }

}
