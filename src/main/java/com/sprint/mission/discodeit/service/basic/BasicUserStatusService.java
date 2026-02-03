package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
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
    public UserStatusResponse create(UserStatusCreateRequest request) {
        // 관련 유저가 존재하는지 확인
        if (userRepository.findById(request.userId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }

        // 해당 유저의 UserStatus가 존재하는지 확인
        userStatusRepository.findByUserId(request.userId()).ifPresent(us -> {
            throw new IllegalStateException("해당 유저의 상태 정보가 이미 존재합니다.");
        });

        UserStatus userStatus = new UserStatus(request.userId(), request.isActive(), Instant.now());
        userStatusRepository.save(userStatus);

        return convertToResponse(userStatus);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        return convertToResponse(getOrThrowUserStatus(id));
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = getOrThrowUserStatus(id);

        userStatus.updateStatus(request.isActive());
        userStatusRepository.save(userStatus);

        return convertToResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 상태 정보를 찾을 수 없습니다."));

        userStatus.updateStatus(request.isActive());
        userStatusRepository.save(userStatus);

        return convertToResponse(userStatus);
    }

    @Override
    public void deleteById(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("해당 상태 정보를 찾을 수 없습니다.");
        }
        userStatusRepository.deleteById(id);
    }


    private UserStatus getOrThrowUserStatus(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상태 정보를 찾을 수 없습니다."));
    }

    private UserStatusResponse convertToResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isActive(),
                userStatus.getLastActiveAt(),
                userStatus.isOnline(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }
}
