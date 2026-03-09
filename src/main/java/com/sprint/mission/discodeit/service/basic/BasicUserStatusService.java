package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
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
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        UserStatus status = findStatusByIdOrThrow(dto.userStatusId());
        status.updateLastActiveAt(status.getLastActiveAt());

        return userStatusMapper.toDto(status);
    }

    @Override
    public UserStatusDto updateStatusByUserId(
            UUID userId, UpdateStatusByUserIdRequestDTO dto
            ) {
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        findUserByIdOrThrow(userId);
        UserStatus status = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "해당 userId에 대한 UserStatus가 존재하지 않습니다 userId=" + userId
                        ));

        status.updateLastActiveAt(dto.newLastActiveAt());
        return userStatusMapper.toDto(status);
    }

    // User 삭제시 같이 삭제되지만 일단 테스트용으로만 둠
    @Override
    public void deleteStatus(UUID userStatusId) {
        findStatusByIdOrThrow(userStatusId);

        userStatusRepository.deleteById(userStatusId);
    }

    private UserStatus findStatusByIdOrThrow(UUID statusId) {
        Objects.requireNonNull(statusId, "userStatusId는 null값일 수 없습니다.");

        return userStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 userStatus가 존재하지 않습니다."));
    }

    private User findUserByIdOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 사용자가 존재하지 않습니다."));
    }
}
