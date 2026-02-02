package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatusResponseDTO createUserStatus(CreateUserStatusRequestDTO dto) {
        // DTO 검증
        checkCreateDTOHasNull(dto);
        // DTO 보정
        CreateUserStatusRequestDTO normalized = normalizeDTO(dto);

        findUserByIdOrThrow(dto.userId());

        if (userStatusRepository.existsByUserId(normalized.userId())) {
            throw new IllegalArgumentException("이미 UserStatus가 존재합니다. userId=" + dto.userId());
        }

        UserStatus status = UserStatusMapper.toEntity(normalized);
        userStatusRepository.save(status);

        return UserStatusMapper.toResponse(status);
    }

    @Override
    public UserStatusResponseDTO findByUserStatusId(UUID userStatusId) {
        UserStatus status = findStatusByIdOrThrow(userStatusId);

        return UserStatusMapper.toResponse(status);
    }

    @Override
    public List<UserStatusResponseDTO> findAll() {
        return UserStatusMapper.toResponseList(userStatusRepository.findAll());
    }

    @Override
    public UserStatusResponseDTO updateUserStatus(UpdateStatusByStatusIdRequestDTO dto) {
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        if (dto.statusType() == null) {
            throw new IllegalArgumentException("statusType은 null값일 수 없습니다.");
        }

        UserStatus status = findStatusByIdOrThrow(dto.userStatusId());
        status.updateStatusType(dto.statusType());

        userStatusRepository.save(status);

        return UserStatusMapper.toResponse(status);
    }

    @Override
    public UserStatusResponseDTO updateStatusByUserId(
            UUID userId, UpdateStatusByUserIdRequestDTO dto
            ) {
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        if (dto.statusType() == null) {
            throw new IllegalArgumentException("statusType은 null값일 수 없습니다.");
        }

        findUserByIdOrThrow(userId);
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "해당 userId에 대한 UserStatus가 존재하지 않습니다 userId=" + userId
                        ));
        status.updateStatusType(dto.statusType());

        userStatusRepository.save(status);
        return UserStatusMapper.toResponse(status);
    }

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

    private void checkCreateDTOHasNull(CreateUserStatusRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto는 null일 수 없습니다.");
        }

        if (dto.userId() == null) {
            throw new IllegalArgumentException("dto의 userId값은 null일 수 없습니다.");
        }
    }

    private CreateUserStatusRequestDTO normalizeDTO(
            CreateUserStatusRequestDTO dto
    ) {
        Instant lastLoginAt = dto.lastLoginAt();

        if (dto.lastLoginAt() == null) {
            lastLoginAt = Instant.now();
        }

        return new CreateUserStatusRequestDTO(dto.userId(), lastLoginAt);
    }
}
