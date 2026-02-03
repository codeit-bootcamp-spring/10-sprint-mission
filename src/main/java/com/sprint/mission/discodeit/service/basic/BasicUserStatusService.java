package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UUID createUserStatus(CreateUserStatusRequest request) {
        validateUserExists(request.userId());
        validateDuplicateUserStatus(request);

        User user = getUserOrThrow(request.userId());

        UserStatus userStatus = new UserStatus(user.getId(), user.getUpdatedAt());
        userStatusRepository.save(userStatus);

        return userStatus.getId();
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 userId 입니다. userId: " + userId);
        }
    }

    private void validateDuplicateUserStatus(CreateUserStatusRequest request) {
        if (userStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("이미 존재하는 userStatus 입니다 userId: " + request.userId());
        }
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    public UserStatusResponse findUserStatusByUserStatusId(UUID userStatusId) {
        UserStatus userStatus = getUserStatusOrThrow(userStatusId);

        return UserStatusResponse.of(userStatus, userStatus.getOnlineStatus());
    }

    public List<UserStatusResponse> findAllUserStatus() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream().map(userStatus ->
                UserStatusResponse.of(userStatus, userStatus.getOnlineStatus())
        ).toList();
    }

    public UserStatusResponse updateUserStatusByUserId(UUID userId) {
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

        userStatus.markActive();
        userStatusRepository.save(userStatus);

        return UserStatusResponse.of(userStatus, userStatus.getOnlineStatus());
    }

    private UserStatus getUserStatusByUserIdOrThrow(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus 를 찾을 수 없습니다 userId: " + userId));
    }


    public void deleteUserStatus(UUID userStatusId) {
        userStatusRepository.deleteById(userStatusId);
    }

    private UserStatus getUserStatusOrThrow(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus 를 찾을 수 없습니다 userStatusId: " + userStatusId));
    }

}
