package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusRequestCreateDto request) {
        Validators.requireNonNull(request, "request");
        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        boolean exists = userStatusRepository.findAll().stream()
                .anyMatch(us -> us.getUserId().equals(request.userId()));

        if(exists) {
            throw new IllegalArgumentException("UserStatus가 이미 존재합니다.");
        }
        UserStatus userStatus = new UserStatus(request.userId(), Instant.now());
        boolean online = userStatus.isOnline();
        return toDto(userStatusRepository.save(userStatus), online);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = validateExistenceUserStatus(id);
        return toDto(userStatus, userStatus.isOnline());
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(us -> toDto(us, us.isOnline()))
                .toList();
    }

    @Override
    public void update(UserStatusRequestUpdateDto request) {
        Validators.requireNonNull(request, "request");
        UserStatus userStatus = validateExistenceUserStatus(request.userId());
        userStatus.updateLastSeenAt();
        userStatusRepository.save(userStatus);
    }

    @Override
    public void updateByUserId(UUID userid) {
        Validators.requireNonNull(userid, "userid는 null이 될 수 없습니다.");
        UserStatus status = userStatusRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(userid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 상태 정보를 찾을 수 없습니다."));

        status.updateLastSeenAt();
        userStatusRepository.save(status);
    }

    @Override
    public void delete(UUID id) {
        validateExistenceUserStatus(id);
        userStatusRepository.deleteById(id);
    }

    private UserStatus validateExistenceUserStatus(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus가 존재하지 않습니다."));

    }

    public static UserStatusResponseDto toDto(UserStatus userStatus, Boolean online) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastSeenAt(),
                online
        );
    }
}
