package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
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
    private final BinaryContentService binaryContentService;

    @Override
    public User createUser(UserCreateRequest request, BinaryContentRequest profileImage) {
        validateDuplicateUser(request);

        User user = new User(
                request.username(),
                request.password(),
                request.email()
        );

        saveUserProfileImage(profileImage, user);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), user.getUpdatedAt());
        userStatusRepository.save(userStatus);

        return user;
    }

    private void validateDuplicateUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + request.email());
        }
    }

    private void saveUserProfileImage(BinaryContentRequest profileImage, User user) {
        if (profileImage == null) {
            return;
        }

        UUID binaryContentId = binaryContentService.createBinaryContent(
                user.getId(),
                profileImage
        );
        user.updateProfileId(binaryContentId);
    }

    @Override
    public User findUserByUserID(UUID userId) {
        return getUserOrThrow(userId);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private UserStatus getUserStatusOrThrow(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_STATUS_NOT_FOUND,
                        "UserStatus 찾을 수 없습니다 userId: " + userId));
    }

    @Override
    public List<UserDto> findAllUsers() {
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
                    return UserDto.of(user, status.getOnlineStatus());
                })
                .toList();
    }

    @Override
    public User updateUser(UUID requestId, UserUpdateRequest request, BinaryContentRequest profileImage) {
        User user = getUserOrThrow(requestId);
        validateDuplicateUserOnUpdate(user, request);

        UUID profileId = binaryContentService.createBinaryContent(
                user.getId(),
                profileImage
        );

        user.update(
                request.newUsername(),
                request.newPassword(),
                request.newEmail(),
                profileId
        );

        userRepository.save(user);

        return user;
    }

    private void validateDuplicateUserOnUpdate(User user, UserUpdateRequest request) {
        String newUsername = request.newUsername();
        if (newUsername != null
                && !newUsername.equals(user.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + newUsername);
        }

        String newEmail = request.newEmail();
        if (newEmail != null
                && !newEmail.equals(user.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + newEmail);
        }
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
            binaryContentService.deleteBinaryContent(profileId);
        }
    }
}
