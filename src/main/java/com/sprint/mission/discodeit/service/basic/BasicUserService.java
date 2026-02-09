package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.function.ThrowingFunction;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreation;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserInfoUpdate;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UsernamePassword;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.DomainRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public UserResponse find(UsernamePassword model) throws IOException {
        User user = userRepository.findByUsernameAndEmail(model.username(), model.password())
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
    public UserResponse create(UserCreation model) throws IOException {
        validateUserUniqueness(model);

        BinaryContent profileImage = registerProfileImage(model);
        UUID profileImageId = profileImage == null ? null : profileImage.getId();
        User user = new User(model.username(), model.email(), model.password(), profileImageId);
        UserStatus userStatus = new UserStatus(user.getId(), model.lastActiveAt());
        userStatusRepository.save(userStatus);
        userRepository.save(user);
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public UserResponse update(UserInfoUpdate model) throws IOException, ClassNotFoundException {
        User user = findById(model.userId());
        user.update(model);
        userRepository.save(user);
        UserStatus userStatus = findUserStatusByUserId(model.userId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public void delete(UUID userId) throws IOException, ClassNotFoundException {
        UserResponse userResponse = findById(userId).toResponse(true);
        deleteIfExist(userResponse.profileId(), "Profile", profileRepository);
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow();
        userStatusRepository.deleteById(status.getId());
        userRepository.deleteById(userId);
    }

    private <T extends DomainRepository<?>> void deleteIfExist(UUID id, String type, T repository) throws IOException {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(type, id));
        }
        repository.deleteById(id);
    }

    private void validateUserUniqueness(UserCreation model) throws IOException {
        if (userRepository.existsByUsername(model.username())) {
            throw new IllegalArgumentException("username, %s, exist already.".formatted(model.username()));
        }
        if (userRepository.existsByEmail(model.email())) {
            throw new IllegalArgumentException("email, %s, exist already.".formatted(model.email()));
        }
    }

    private BinaryContent registerProfileImage(UserCreation model) throws IOException {
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

    @Override
    protected User findById(UUID id) throws IOException, ClassNotFoundException {
        return findEntityById(id, "User", userRepository);
    }
}
