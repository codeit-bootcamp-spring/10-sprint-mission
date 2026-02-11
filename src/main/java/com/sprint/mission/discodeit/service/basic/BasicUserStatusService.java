package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus createUserStatus(UserStatusCreateRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));

        if (userStatusRepository.existUserStatus(request.userId())) {
            throw new IllegalArgumentException("이미 존재하는 UserStatus가 있습니다.");
        }

        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus findUserStatusById(UUID userStatusId) {
        return validateAndGetUserStatusByUserStatusId(userStatusId);
    }

    @Override
    public List<UserStatus> findAllUserStatus() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus updateUserStatus(UserStatusUpdateRequest request) {
        if (request.lastOnlineTime() == null) {
            throw new IllegalArgumentException("lastOnlineTime null로 입력되었습니다.");
        }

        UserStatus userStatus = validateAndGetUserStatusByUserStatusId(request.userStatusId());

        userStatus.updateLastOnlineTime(request.lastOnlineTime());
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
        validateUserByUserId(userId);
        UserStatus userStatus = validateAndGetUserStatusByUserId(userId);

        userStatus.updateLastOnlineTime(request.lastOnlineTime());
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public void deleteUserStatus(UUID userStatusId) {
        validateUserStatusByUserStatusId(userStatusId);
        userStatusRepository.delete(userStatusId);
    }

    //// validation
    // user ID null & user 객체 존재 확인
    public void validateUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    public UserStatus validateAndGetUserStatusByUserStatusId(UUID userStatusId) {
        ValidationMethods.validateId(userStatusId);
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus가 없습니다."));
    }
    public UserStatus validateAndGetUserStatusByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus가 없습니다."));
    }
    public void validateUserStatusByUserStatusId(UUID userStatusId) {
        ValidationMethods.validateId(userStatusId);
         userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus가 없습니다."));
    }
}
