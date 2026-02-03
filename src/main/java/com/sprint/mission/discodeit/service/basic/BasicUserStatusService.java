package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("유저를 찾을 수 없습니다.");
        }

        userStatusRepository.findByUserId(dto.getUserId())
                .ifPresent(s -> { throw new IllegalStateException("이 유저의 UserStatus가 이미 존재합니다."); });

        UserStatus userStatus = new UserStatus(dto.getUserId());
        userStatusRepository.save(userStatus);
        return convertToDto(userStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        return userStatusRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));
    }

    @Override
    public UserStatusResponseDto findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NoSuchElementException("이 유저의 UserStatus를 찾을 수 없습니다."));
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));

        userStatus.updateStatus();
        userStatusRepository.save(userStatus);
        return convertToDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private UserStatusResponseDto convertToDto(UserStatus status) {
        return new UserStatusResponseDto(
                status.getId(),
                status.getUserId(),
                status.getLastActiveAt(),
                status.isOnline()
        );
    }
}