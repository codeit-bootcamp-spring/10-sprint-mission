package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
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

        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }

        if (userStatusRepository.findByUserId(userId) != null) {
            throw new IllegalStateException("UserStatus already exists for userId=" + userId);
        }

        UserStatus saved = userStatusRepository.save(new UserStatus(userId, Instant.now()));
        return saved.getId();
    }

    @Override
    public UserStatusResponse find(UUID id) {
        requireNonNull(id, "id");

        UserStatus status = userStatusRepository.findById(id);
        if (status == null) {
            throw new StatusNotFoundException();
        }

        return toDto(status);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID id) {
        requireNonNull(id, "id");

        UserStatus status = userStatusRepository.findById(id);
        if (status == null) {
            throw new StatusNotFoundException();
        }

        status.updateLastSeenAt(Instant.now());
        userStatusRepository.save(status);

        return toDto(status);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }

        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) {
            throw new StatusNotFoundException();
        }

        status.updateLastSeenAt(Instant.now());
        userStatusRepository.save(status);

        return toDto(status);
    }

    @Override
    public UserStatusResponse updateOnline(UUID userId, boolean online) {
        requireNonNull(userId, "userId");

        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }

        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) {
            throw new StatusNotFoundException();
        }

        status.updateOnline(online);
        userStatusRepository.save(status);

        return toDto(status);
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");

        if (userStatusRepository.findById(id) == null) {
            throw new StatusNotFoundException();
        }
        userStatusRepository.delete(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }
        if (userStatusRepository.findByUserId(userId) == null) {
            throw new StatusNotFoundException();
        }

        userStatusRepository.deleteByUserId(userId);
    }

    private UserStatusResponse toDto(UserStatus status) {
        return new UserStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getLastSeenAt(),
                status.isOnline()
        );
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
