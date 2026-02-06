package com.sprint.mission.discodeit.service.basic;

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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository profileRepository;
    private final UserStatusRepository userStatusRepository;
    private final String ID_NOT_FOUND = "%s with id %s not found";

    @Override
    public UserResponse find(UsernamePassword model) {
        User user = userRepository.findAll()
                .stream()
                .filter(user1 -> user1.matchUsername(model.username()))
                .filter(user1 -> user1.matchPassword(model.password()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("incorrect username or password"));
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = findEntityById(userId, "User", userRepository);
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus status = findUserStatusByUserId(user.getId());
                    return user.toResponse(status.isActive());
                })
                .toList();
    }

    @Override
    public UserResponse create(UserCreation model) {
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
    public UserResponse update(UserInfoUpdate model) {
        User user = findEntityById(model.userId(), "User", userRepository);
        user.update(model);
        userRepository.save(user);
        UserStatus userStatus = findUserStatusByUserId(model.userId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public void delete(UUID userId) {
        UserResponse userResponse = findEntityById(userId, "User", userRepository).toResponse(true);
        deleteIfExist(userResponse.profileId(), "Profile", profileRepository);
        UserStatus status = userStatusRepository.findByUserId(userId)
                        .orElseThrow();
        userStatusRepository.deleteById(status.getId());
        userRepository.deleteById(userId);
    }

    private <T extends DomainRepository<?>> void deleteIfExist(UUID id, String type, T repository) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(type, id));
        }
        repository.deleteById(id);
    }

    private void validateUserUniqueness(UserCreation model) {
        if (userRepository.existsByUsername(model.username())) {
            throw new IllegalArgumentException("username, %s, exist already.".formatted(model.username()));
        }
        if (userRepository.existsByEmail(model.email())) {
            throw new IllegalArgumentException("email, %s, exist already.".formatted(model.email()));
        }
    }

    private BinaryContent registerProfileImage(UserCreation model) {
        if (model.profileImage() == null) {
            return null;
        }
        BinaryContent profileImage = new BinaryContent(model.profileImage());
        return profileRepository.save(profileImage);
    }

    private <T> T findEntityById(UUID id, String type, DomainRepository<T> repository) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted(type, id)));
    }

    private UserStatus findUserStatusByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted("User", userId)));
    }

}
