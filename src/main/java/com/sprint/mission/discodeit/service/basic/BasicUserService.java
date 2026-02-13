package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.function.ThrowingFunction;
import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService extends BasicDomainService<User> implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository profileRepository;
    private final UserStatusRepository userStatusRepository;
    private final IdGenerator idGenerator;

    @Override
    public UserResponse find(LoginRequest request) throws IOException {
        User user = userRepository.filter(user1 -> user1.matchUsername(request.username()))
                .filter(user1 -> user1.matchPassword(request.password()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("username or password incorrect"));
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public UserResponse find(UUID userId) throws IOException, ClassNotFoundException {
        User user = findById(userId);
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public List<UserResponse> findAll() throws IOException {
        return userRepository.streamAll(stream ->
                        stream.map(ThrowingFunction.unchecked(this::getUserResponse)))
                .toList();
    }

    @Override
    public UserResponse create(UserCreateRequest request) throws IOException {
        validateUserUniqueness(request);

        BinaryContent profileImage = registerProfileImage(request);
        UUID profileImageId = profileImage == null ? null : profileImage.getId();
        User user = new User(idGenerator.generateId(), request.username(), request.email(),
                request.password(), profileImageId);
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);
        userRepository.save(user);
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public UserResponse update(UserUpdateRequest request) throws IOException, ClassNotFoundException {
        User user = findById(request.userId());
        user.update(request);
        userRepository.save(user);
        UserStatus userStatus = findUserStatusByUserId(request.userId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public void delete(UUID userId) throws IOException, ClassNotFoundException {
        User user = findById(userId);
        UserStatus status = findUserStatusByUserId(userId);
        UserResponse userResponse = user.toResponse(status.isActive());
        deleteIfExist(userResponse.profileId(), "Profile", profileRepository);
        deleteIfExist(status.getId(), "UserStatus", userStatusRepository);
        deleteIfExist(userId, "User", userRepository);
    }

    @Override
    public UserResponse updateActiveAt(UUID id) throws IOException, ClassNotFoundException {
        UserStatus status = findUserStatusByUserId(id);
        status.update();
        userStatusRepository.save(status);
        return findById(id).toResponse(status.isActive());
    }

    @Override
    protected User findById(UUID id) throws IOException, ClassNotFoundException {
        return findEntityById(id, "User", userRepository);
    }

    private void validateUserUniqueness(UserCreateRequest model) throws IOException {
        if (userRepository.existsByUsername(model.username())) {
            throw new IllegalArgumentException("username, %s, exist already.".formatted(model.username()));
        }
        if (userRepository.existsByEmail(model.email())) {
            throw new IllegalArgumentException("email, %s, exist already.".formatted(model.email()));
        }
    }

    private BinaryContent registerProfileImage(UserCreateRequest model) throws IOException {
        if (model.profileImage() == null) {
            return null;
        }
        BinaryContent profileImage = new BinaryContent(model.profileImage());
        return profileRepository.save(profileImage);
    }

    private UserStatus findUserStatusByUserId(UUID userId) throws IOException {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted("User", userId)));
    }

    private UserResponse getUserResponse(User user) throws IOException {
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }
}
