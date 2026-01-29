package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID createUser(CreateUserRequest createUserRequest) {
        validateDuplicateUser(createUserRequest);

        User user = new User(
                createUserRequest.userName(),
                createUserRequest.password(),
                createUserRequest.email()
        );
        userRepository.save(user);

        saveUserProfileImage(createUserRequest, user);

        UserStatus userStatus = new UserStatus(user.getId(), user.getUpdatedAt());
        userStatusRepository.save(userStatus);

        return user.getId();
    }

    private void validateDuplicateUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.userName())) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다 username: " + createUserRequest.userName());
        }

        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new IllegalArgumentException("이미 존재하는 email 입니다. email: " + createUserRequest.email());
        }
    }

    private void saveUserProfileImage(CreateUserRequest createUserRequest, User user) {
        if (createUserRequest.image() == null) return;

        BinaryContent binaryContent = BinaryContent.forUser(
                user.getId(),
                createUserRequest.image()
        );

        user.updateProfileId(binaryContent.getId());
        binaryContentRepository.save(binaryContent);
        userRepository.save(user);
    }

    @Override
    public UserResponse findUserByUserID(UUID userId) {
        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusOrThrow(userId);

        return UserResponse.from(user, userStatus.getOnlineStatus());
    }

    private @NonNull User getUserOrThrow(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
        return user;
    }

    private @NonNull UserStatus getUserStatusOrThrow(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus 찾을 수 없습니다 userId: " + userId));
        return userStatus;
    }

    @Override
    public List<UserResponse> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserStatus> statuses = userStatusRepository.findAll();

        Map<UUID, UserStatus> statusMap = statuses.stream()
                .collect(Collectors.toMap(
                        UserStatus::getUserId,
                        Function.identity()
                ));

        return users.stream()
                .map(user -> {
                    UserStatus status = statusMap.get(user.getId());
                    return UserResponse.from(user, status.getOnlineStatus());
                })
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID requestId, UpdateUserRequest request) {
        User user = getUserOrThrow(requestId);
        UUID profileId = user.getProfileId();

        deleteBinaryContentById(profileId);

        BinaryContent binaryContent = BinaryContent.forUser(user.getId(), request.image());
        binaryContentRepository.save(binaryContent);

        user.update(
                request.username(),
                request.password(),
                request.email(),
                binaryContent.getId()
        );

        userRepository.save(user);

        UserStatus userStatus = getUserStatusOrThrow(user.getId());
        return UserResponse.from(user, userStatus.getOnlineStatus());
    }

    @Override
    public void deleteUser(UUID requestId) {
        User user = getUserOrThrow(requestId);

        deleteBinaryContentById(user.getProfileId());

        userStatusRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    private void deleteBinaryContentById(UUID profileId) {
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }
    }
}
