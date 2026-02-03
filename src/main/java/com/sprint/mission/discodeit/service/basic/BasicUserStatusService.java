package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusResponse create(UserStatusCreateRequest request) {
        // 유저 상태 생성을 위한 필수 검증
        validateCreateUserStatus(request);

        // 유저 상태 생성 및 저장
        UserStatus userStatus = new UserStatus(request.userId(), request.status());
        userStatusRepository.save(userStatus);

        return UserStatusResponse.from(userStatus);
    }

    public UserStatusResponse findById(UUID userStatusId) {
        if (userStatusId == null) {
            throw new RuntimeException("유저 상태가 존재하지 않습니다.");
        }

        // 유저 상태가 존재하는지 검색 및 검증
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        return UserStatusResponse.from(userStatus);
    }

    public List<UserStatusResponse> findAll() {
        List<UserStatusResponse> responses = new ArrayList<>();
        // 유저 상태 목록 조회 및 순회
        for (UserStatus userStatus : userStatusRepository.findAll()) {
            // 응답 DTO 생성 후 반환 리스트에 추가
            responses.add(UserStatusResponse.from(userStatus));
        }

        return responses;
    }

    public UserStatusResponse update(UserStatusUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        UserStatus userStatus = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        if (request.status() != null) {
            userStatus.changeStatus(request.status());
        } else {
            userStatus.update();
        }

        userStatusRepository.save(userStatus);

        return UserStatusResponse.from(userStatus);
    }

    public UserStatusResponse updateByUserId(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        userStatus.update();
        userStatusRepository.save(userStatus);

        return UserStatusResponse.from(userStatus);
    }

    public void delete(UUID userStatusId) {
        if (userStatusId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 유저 상태가 존재하는지 검증
        userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        userStatusRepository.delete(userStatusId);
    }

    private void validateCreateUserStatus(UserStatusCreateRequest request) {
        if (request == null || request.userId() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new RuntimeException("유저 상태가 이미 존재합니다.");
        }

        if (request.status() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
    }
}
