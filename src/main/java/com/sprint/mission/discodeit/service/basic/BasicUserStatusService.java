package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest) {
        UUID userId = userStatusCreateRequest.userId();
        Instant lastActiveAt = userStatusCreateRequest.lastActiveAt();
        // 관련된 User가 존재하지 않으면 예외를 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(userId+"를 가진 User를 찾지 못했습니다"));
        // 같은 User와 관련된 객체가 이미 존재하면 예외를 발생
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException(userId+"를 가진 UserState가 이미 존재합니다");
        }
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusDto find(UUID userStatusId) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        return userStatusMapper.toDto(processUpdate(userStatus, userStatusUpdateRequest));
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("userId: "+userId+"를 가진 UserStatus를 찾지 못했습니다"));
        return userStatusMapper.toDto(processUpdate(userStatus, userStatusUpdateRequest));
    }

    private UserStatus processUpdate(UserStatus userStatus, UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant lastActiveAt = userStatusUpdateRequest.newLastActiveAt();
        userStatus.updateLastActiveAt(lastActiveAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if(!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("userStatusId: "+ userStatusId + "를 가진 UserStatus를 찾지 못했습니다");
        }
        userStatusRepository.deleteById(userStatusId);
    }

    // UserStatusRepository.findById()를 통한 반복되는 UserStatus 조회/예외처리를 중복제거 하기 위한 메서드
    private UserStatus getUserStatusByIdOrThrow(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("userStatusId:"+userStatusId+"를 가진 UserStatus를 찾지 못했습니다"));
    }
}
