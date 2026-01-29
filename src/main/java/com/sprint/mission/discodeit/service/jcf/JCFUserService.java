package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;    // 유저 전체 목록

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public UserDto.UserResponse create(UserDto.UserCreateRequest request) {
        Objects.requireNonNull(request.name(), "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(request.email(), "이메일은 필수 항목입니다.");

        if (data.values().stream().anyMatch(user -> user.getName().equals(request.name()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (data.values().stream().anyMatch(user -> user.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(request.name(), request.email());
        data.put(user.getId(), user);

        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public UserDto.UserResponse findById(UUID userId) {
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public List<UserDto.UserResponse> findAll() {
        return data.values().stream()
                .map(user -> UserDto.UserResponse.from(user, new UserStatus(user)))
                .toList();
    }

    @Override
    public UserDto.UserResponse update(UUID userId, UserDto.UserUpdateRequest request) {
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        Optional.ofNullable(request.name()).ifPresent(user::updateName);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        data.put(userId, user);

        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public void delete(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("해당 유저가 존재하지 않습니다.");
        }
        data.remove(userId);
    }

}
