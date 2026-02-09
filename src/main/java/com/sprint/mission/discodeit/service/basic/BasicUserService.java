package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
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
    public UUID createUser(CreateUserRequest request) {
        validateDuplicateUser(request);

        User user = new User(
                request.username(),
                request.password(),
                request.email()
        );

        user = saveUserProfileImage(request, user);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), user.getUpdatedAt());
        userStatusRepository.save(userStatus);

        return user.getId();
    }

    private void validateDuplicateUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다 username: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 email 입니다. email: " + request.email());
        }
    }

    private User saveUserProfileImage(CreateUserRequest request, User user) {
        if (request.profileImage() == null) return user;

        BinaryContent binaryContent = new BinaryContent(
                user.getId(),
                request.profileImage().type(),
                request.profileImage().image()
        );

        user.updateProfileId(binaryContent.getId());
        binaryContentRepository.save(binaryContent);

        return user;
    }

    @Override
    public UserResponse findUserByUserID(UUID userId) {
        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusOrThrow(userId);

        return UserResponse.of(user, userStatus.getOnlineStatus());
    }

    private User getUserOrThrow(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
        return user;
    }

    private UserStatus getUserStatusOrThrow(UUID userId) {
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
                    return UserResponse.of(user, status.getOnlineStatus());
                })
                .toList();
    }

    //TODO: validation 없음
    @Override
    public UserResponse updateUser(UUID requestId, UpdateUserRequest request) {
        User user = getUserOrThrow(requestId);

        UUID profileId = createBinaryContentIfExist(request, user);

        user.update(
                request.username(),
                request.password(),
                request.email(),
                profileId
        );

        userRepository.save(user);

        UserStatus userStatus = getUserStatusOrThrow(user.getId());
        return UserResponse.of(user, userStatus.getOnlineStatus());
    }

    private UUID createBinaryContentIfExist(UpdateUserRequest request, User user) {
        if (request.profileImage() == null) {
            return null;
        }

        BinaryContent binaryContent = new BinaryContent(
                user.getId(),
                request.profileImage().type(),
                request.profileImage().image()
        );

        binaryContentRepository.save(binaryContent);
        return  binaryContent.getId();
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
