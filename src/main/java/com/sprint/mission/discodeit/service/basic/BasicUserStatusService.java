package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper mapper;

    @Override
    public UserStatusDto createUserStatus(UserStatusDto.UserStatusCreateRequest createReq) {
        UUID userId = createReq.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        userStatusRepository.findByUserId(userId)
                .ifPresent(u -> { throw new BusinessLogicException(ErrorCode.USERSTATUS_ALREADY_EXISTS); });

        UserStatus status = new UserStatus();
        status.updateUser(user);
        userStatusRepository.save(status);

        return toResponse(status);
    }

    @Override
    public UserStatusDto findById(UUID uuid) {
        return userStatusRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusDto updateUserStatus(UUID uuid, UserStatusDto.UserStatusUpdateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusDto.UserStatusUpdateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public void deleteUserStatusById(UUID uuid) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

        userStatusRepository.deleteById(uuid);
    }

    private UserStatusDto toResponse(UserStatus userStatus) {
        return mapper.toDto(userStatus);
    }
}
