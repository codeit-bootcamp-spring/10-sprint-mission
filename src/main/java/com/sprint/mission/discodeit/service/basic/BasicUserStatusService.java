package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        // 관련된 User가 존재하지 않으면 예외를 발생
        if(!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다. userId: " + request.userId());
        }
        // 같은 User와 관련된 객체가 이미 존재하면 예외를 발생
        if(userStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("이미 해당 유저의 상태 정보(UserStatus)가 존재합니다. userId: " + request.userId());
        }

        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));
        return toResponse(userStatus);
    }


    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정할 UserStatus를 찾을 수 없습니다."));

        userStatus.renewActivity();

        userStatusRepository.save(userStatus);
        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("수정할 UserStatus를 찾을 수 없습니다."));

        userStatus.renewActivity();

        userStatusRepository.save(userStatus);
        return toResponse(userStatus);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 UserStatus를 찾을 수 없습니다."));

        userStatusRepository.deleteById(id);
    }

    // ===

    private UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}