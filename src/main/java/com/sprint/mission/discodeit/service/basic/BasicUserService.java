package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.status.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(CreateUserRequest request) {
        // 이미 작성된 코드
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        // 디버깅: ID가 null인지 확인
        System.out.println("User created with ID: " + user.getId());

        if (user.getId() == null) {
            throw new IllegalStateException("User ID is null after creation!");
        }
        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId(), "OFFLINE", Instant.now(),Instant.now());
        userStatusRepository.save(userStatus);

        return UserResponse.from(savedUser, userStatus);
    }

    @Override
    public UserResponse find(UUID userId) {
        // 이미 작성된 코드
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElse(null);

        return UserResponse.from(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        // 이미 작성된 코드
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
                    return UserResponse.from(user, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));

        // 중복 체크 (변경하려는 값이 있을 때만)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            validateDuplicateUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateDuplicateEmail(request.getEmail());
        }

        // User의 update() 메서드 한 번에 호출
        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 저장
        User updatedUser = userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElse(null);
        return UserResponse.from(updatedUser, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        // 이미 작성된 코드
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));

        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }

        userStatusRepository.findByUserId(userId)
                .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        userRepository.deleteById(userId);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }
}

