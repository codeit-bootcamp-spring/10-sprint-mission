package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusCreation;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusUpdate;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final String ID_NOT_FOUND = "%s with id, %s, not found";
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreation model) {
        User user = findUser(model.userId());
        if (userStatusRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException("UserStatus already exist");
        }
        UserStatus status = new UserStatus(user.getId(), model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public UserStatusResponse find(UUID id) {
        return findById(id).toResponse();
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(UserStatus::toResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdate model) {
        UserStatus status = findById(Objects.requireNonNull(model.id()));
        status.update(model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdate model) {
        UserStatus status = findByUserId(Objects.requireNonNull(model.userId()));
        status.update(model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException(
                    ID_NOT_FOUND.formatted("User Profile", id));
        }
        userStatusRepository.deleteById(id);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", userId)));
    }

    private UserStatus findById(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User Profile", id)));
    }

    private UserStatus findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User Profile", userId)));
    }
}
