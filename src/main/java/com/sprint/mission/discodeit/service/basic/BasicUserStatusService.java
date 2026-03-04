package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto.userStatusResponse createUserStatus(UserStatusDto.userStatusCreateRequest createReq) {
        UUID userId = createReq.userId();
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        userStatusRepository.findByUserId(userId)
                .ifPresent(u -> { throw new BusinessLogicException(ErrorCode.USERSTATUS_ALREADY_EXISTS); });

        UserStatus userStatus = new UserStatus(userId);
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusDto.userStatusResponse findById(UUID uuid) {
        return userStatusRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));
    }

    @Override
    public List<UserStatusDto.userStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusDto.userStatusResponse updateUserStatus(UUID uuid, UserStatusDto.userStatusUpdateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatus.updateLastActiveAt(updateReq.lastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusDto.userStatusResponse updateUserStatusByUserId(UUID userId, UserStatusDto.userStatusUpdateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatus.updateLastActiveAt(updateReq.lastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public void deleteUserStatusById(UUID uuid) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatusRepository.deleteById(uuid);
    }

    private UserStatusDto.userStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusDto.userStatusResponse(userStatus.getId(), userStatus.getCreatedAt(), userStatus.getUpdatedAt(),
                userStatus.getUserId(), userStatus.getLastActiveAt(), userStatus.isOnline());
    }
}
