package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest) {
        UUID userId = userStatusCreateRequest.userId();

        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(ErrorCode.USER_STATUS_ALREADY_EXISTS);
        }

        Instant lastActiveAt = userStatusCreateRequest.lastActiveAt();
        UserStatus userStatus = new UserStatus(userId, lastActiveAt);
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));
        return toDto(userStatus);
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastActiveAt = userStatusUpdateRequest.newLastActiveAt();
        if (newLastActiveAt == null) {
            newLastActiveAt = Instant.now();
        }

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));

        userStatus.update(newLastActiveAt);
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastActiveAt = userStatusUpdateRequest.newLastActiveAt();
        if (newLastActiveAt == null) {
            newLastActiveAt = Instant.now();
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));

        userStatus.update(newLastActiveAt);
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND);
        }

        userStatusRepository.deleteById(userStatusId);
    }

    private UserStatusDto toDto(UserStatus userStatus) {
        return new UserStatusDto(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.isOnline()
        );
    }
}
