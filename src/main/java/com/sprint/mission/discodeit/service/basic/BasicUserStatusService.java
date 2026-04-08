package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UUID createUserStatus(CreateUserStatusRequest request) {
        User user = getUserOrThrow(request.userId());
        return createUserStatus(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusDto findUserStatusByUserStatusId(UUID userStatusId) {
        return userStatusMapper.toDto(getUserStatusOrThrow(userStatusId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAllUserStatus() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

        userStatus.updateLastActiveAt(request.newLastActiveAt());

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void deleteUserStatus(UUID userStatusId) {
        UserStatus userStatus = getUserStatusOrThrow(userStatusId);
        userStatus.assignUser(null);
        userStatusRepository.delete(userStatus);
    }

    @Override
    @Transactional
    public UUID createUserStatus(User user) {
        validateDuplicateUserStatus(user.getId());

        UserStatus userStatus = new UserStatus(user, user.getUpdatedAt());
        userStatusRepository.save(userStatus);

        return userStatus.getId();
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "사용자를 찾을 수 없습니다 userId: " + userId,
                        Map.of("userId", userId)
                ));
    }

    private void validateDuplicateUserStatus(UUID userId) {
        if (userStatusRepository.existsByUserId(userId)) {
            throw new UserStatusAlreadyExistsException(
                    "이미 존재하는 userStatus 입니다 userId: " + userId,
                    Map.of("userId", userId)
            );
        }
    }

    private UserStatus getUserStatusOrThrow(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                        "UserStatus 를 찾을 수 없습니다 userStatusId: " + userStatusId,
                        Map.of("userStatusId", userStatusId)
                ));
    }

    private UserStatus getUserStatusByUserIdOrThrow(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                        "UserStatus 를 찾을 수 없습니다 userId: " + userId,
                        Map.of("userId", userId)
                ));
    }
}
