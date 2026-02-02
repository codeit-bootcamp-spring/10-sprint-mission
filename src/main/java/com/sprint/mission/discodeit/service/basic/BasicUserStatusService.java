package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (userRepository.findById(request.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저 상태입니다.");
        }
        UserStatus userStatus = new UserStatus(request.getUserId(), LocalDateTime.now());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toResponse(userStatus);
    }

    public UserStatusResponse find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        return userStatusMapper.toResponse(userStatus);
    }

    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        userStatus.updateLastSeen(request.getLastSeen());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toResponse(userStatus);
    }

    public UserStatusResponse updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        userStatus.updateLastSeen(LocalDateTime.now());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toResponse(userStatus);
    }

    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }
}