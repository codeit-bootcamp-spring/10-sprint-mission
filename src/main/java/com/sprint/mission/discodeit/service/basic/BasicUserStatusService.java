package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;

    @Autowired(required = false)
    private UserStatusRepository userStatusRepository;

    @Override
    public UserStatusDto.Response create(UserStatusDto.CreateRequest request) {
        if (!userRepository.existsById(request.userID())) throw new IllegalArgumentException("사용자가 존재하지 않습니다.");

        boolean exists = userStatusRepository.findAll().stream()
                .anyMatch(us -> us.getUserId().equals(request.userID()));
        if (exists) throw new IllegalArgumentException("유저 상태가 이미 존재합니다.");
        UserStatus userStatus = new UserStatus(request.userID(), request.lastOnlineAt());
        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto.Response findById(UUID id) {
        return convertToResponse(findUserStatusEntityById(id));
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserStatusDto.Response update(UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = findUserStatusEntityById(request.id());
        userStatus.update(request.lastOnlineAt());
        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto.Response updateByUserId(UUID userId, Instant lastOnlineAt) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 상태 정보가 없습니다"));

        userStatus.update(lastOnlineAt);
        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public void delete(UUID id) {
        findUserStatusEntityById(id);
        userStatusRepository.deleteById(id);
    }

    // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
    private UserStatus findUserStatusEntityById(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 상태 정보를 찾을 수 없습니다. ID: " + id));
    }

    private UserStatusDto.Response convertToResponse(UserStatus userStatus) {
        return new UserStatusDto.Response(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastOnlineAt(),
                userStatus.isOnline()
        );
    }
}
