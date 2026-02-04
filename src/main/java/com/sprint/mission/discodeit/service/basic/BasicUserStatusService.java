package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UUID create(UUID userId) {
        requireNonNull(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 중복이면 예외
        if (userStatusRepository.findByUserId(userId) != null) {
            throw new IllegalStateException("UserStatus already exists for userId=" + userId);
        }

        UserStatus saved = userStatusRepository.save(new UserStatus(userId, Instant.now()));
        return saved.getId();
    }

    @Override
    public UserStatus find(UUID id) {
        requireNonNull(id, "id");
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + id));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id) {
        requireNonNull(id, "id");

        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + id));

        status.updateLastSeenAt(Instant.now());
        return userStatusRepository.save(status);
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) {
            throw new IllegalArgumentException("UserStatus not found for userId: " + userId);
        }

        status.updateLastSeenAt(Instant.now());
        return userStatusRepository.save(status);
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");
        userStatusRepository.delete(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        requireNonNull(userId, "userId");
        userStatusRepository.deleteByUserId(userId);
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
