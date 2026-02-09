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

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService extends BasicDomainService<UserStatus> implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreation model) throws IOException, ClassNotFoundException {
        User user = findUser(model.userId());
        if (userStatusRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException("UserStatus already exist");
        }
        UserStatus status = new UserStatus(user.getId(), model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public UserStatusResponse find(UUID id) throws IOException, ClassNotFoundException {
        return findById(id).toResponse();
    }

    @Override
    public List<UserStatusResponse> findAll() throws IOException {
        return userStatusRepository.streamAll(stream -> stream.map(UserStatus::toResponse))
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdate model) throws IOException, ClassNotFoundException {
        UserStatus status = findById(Objects.requireNonNull(model.id()));
        status.update(model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdate model) throws IOException {
        UserStatus status = findByUserId(Objects.requireNonNull(model.userId()));
        status.update(model.lastActiveAt());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) throws IOException {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException(
                    ID_NOT_FOUND.formatted("User Profile", id));
        }
        userStatusRepository.deleteById(id);
    }

    @Override
    protected UserStatus findById(UUID id) throws IOException, ClassNotFoundException {
        return findEntityById(id, "UserStatus", userStatusRepository);
    }

    private UserStatus findByUserId(UUID userId) throws IOException {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User Profile", userId)));
    }

    private User findUser(UUID userId) throws IOException, ClassNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", userId)));
    }
}
