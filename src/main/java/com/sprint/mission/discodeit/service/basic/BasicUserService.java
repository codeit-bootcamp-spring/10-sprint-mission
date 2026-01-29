package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto.UserResponse create(UserDto.UserCreateRequest request) {
        Objects.requireNonNull(request.name(), "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(request.email(), "이메일은 필수 항목입니다.");

        // 중복 검사
        if (userRepository.findAll().stream().anyMatch(user -> user.getName().equals(request.name()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }


        User user = new User(request.name(), request.email(), request.profileImageUrl());
        UserStatus userStatus = new UserStatus(user);
        userRepository.save(user);
        userStatusRepository.save(userStatus);

        return UserDto.UserResponse.from(user, userStatus);
    }

    @Override
    public UserDto.UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 없습니다."));

        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public List<UserDto.UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 없습니다."));
                    return UserDto.UserResponse.from(user, status);
                })
                .toList();
    }

    @Override
    public UserDto.UserResponse update(UUID userId, UserDto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        Optional.ofNullable(request.name()).ifPresent(user::updateName);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        Optional.ofNullable(request.profileImageUrl()).ifPresent(user::updateProfileImageUrl);

        userRepository.save(user);
        UserStatus status = userStatusRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 없습니다."));
        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public void delete(UUID userId) {
        // 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        userRepository.delete(userId);
    }
}