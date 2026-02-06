package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    public UserStatusDto.response createUserStatus(UserStatusDto.createRequest createReq) {
        UUID userId = createReq.userId();
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        userStatusRepository.findByUserId(userId)
                .ifPresent(u -> { throw new IllegalStateException("이미 존재하는 userStatus입니다"); });

        UserStatus userStatus = new UserStatus(userId);
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusDto.response findById(UUID uuid) {
        return userStatusRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userStatus입니다"));
    }

    @Override
    public List<UserStatusDto.response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusDto.response updateUserStatus(UUID uuid, UserStatusDto.updateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userStatus입니다"));

        userStatus.updateLastActiveAt(updateReq.lastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusDto.response updateUserStatusByUserId(UUID userId, UserStatusDto.updateRequest updateReq) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userStatus입니다"));

        userStatus.updateLastActiveAt(updateReq.lastActiveAt());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public void deleteUserStatusById(UUID uuid) {
        UserStatus userStatus = userStatusRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userStatus입니다"));

        userStatusRepository.deleteById(uuid);
    }

    private UserStatusDto.response toResponse(UserStatus userStatus) {
        return new UserStatusDto.response(userStatus.getId(), userStatus.getCreatedAt(), userStatus.getUpdatedAt(),
                userStatus.isOnline());
    }
}
