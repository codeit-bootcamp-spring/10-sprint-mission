package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.status.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional(readOnly = true)
    public UserStatusDto findByUserStatusId(UUID userStatusId) {
        UserStatus status = findStatusByIdOrThrow(userStatusId);

        return userStatusMapper.toDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        return userStatusMapper.toResponseList(userStatusRepository.findAll());
    }

    @Override
    public UserStatusDto updateUserStatus(UpdateStatusByStatusIdRequestDTO dto) {
        if (dto == null) {
            throw new InvalidInputException(
                    ErrorCode.DTO_CAN_NOT_BE_NULL, Map.of("dto", "dto is null")
            );
        }

        UserStatus status = findStatusByIdOrThrow(dto.userStatusId());
        status.updateLastActiveAt(status.getLastActiveAt());

        return userStatusMapper.toDto(status);
    }

    @Override
    public UserStatusDto updateStatusByUserId(
            UUID userId, UpdateStatusByUserIdRequestDTO dto
            ) {
        if (dto == null) {
            throw new InvalidInputException(
                    ErrorCode.DTO_CAN_NOT_BE_NULL, Map.of("dto", "dto is null")
            );
        }

        findUserByIdOrThrow(userId);
        UserStatus status = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                {
                    log.warn("[USERSTATUS_NOT_FOUND] 유저 상태가 존재하지 않음: userId={}", userId);
                    return new UserStatusNotFoundException(userId);
                });

        status.updateLastActiveAt(dto.newLastActiveAt());

        log.info("[USERSTATUS_UPDATE_SUCCESS] 유저 상태 수정 성공: userStatusId={}", status.getId());
        return userStatusMapper.toDto(status);
    }

    // User 삭제시 같이 삭제되지만 일단 테스트용으로만 둠
    @Override
    public void deleteStatus(UUID userStatusId) {
        findStatusByIdOrThrow(userStatusId);

        log.info("[USERSTATUS_DELETE_SUCCESS] 유저 상태 삭제 성공: userStatusId={}", userStatusId);
        userStatusRepository.deleteById(userStatusId);
    }

    private UserStatus findStatusByIdOrThrow(UUID statusId) {
        if (statusId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("statusId", "statusId is null")
            );
        }

        return userStatusRepository.findById(statusId)
                .orElseThrow(() -> {
                    log.warn("[USERSTATUS_NOT_FOUND] 유저 상태가 존재하지 않음: userStatusId={}", statusId);
                    return new UserStatusNotFoundException(statusId);
                });
    }

    private User findUserByIdOrThrow(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: userId={}", userId);
                    return new UserNotFoundException(userId);
                });
    }
}
