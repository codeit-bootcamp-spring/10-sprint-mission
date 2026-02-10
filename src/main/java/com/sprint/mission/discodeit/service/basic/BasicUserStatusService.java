package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // 사용자 상태 생성
    @Override
    public UserStatusResponseDTO create(UserStatusCreateRequestDTO userStatusCreateRequestDTO) {
        UserEntity targetUser = userRepository.findById(userStatusCreateRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        if (userStatusRepository.existsById(targetUser.getId())) {
            throw new RuntimeException("이미 사용자의 상태 정보가 존재합니다.");
        }

        UserStatusEntity newUserStatus = new UserStatusEntity(userStatusCreateRequestDTO.userId());
        userStatusRepository.save(newUserStatus);

        return toResponseDTO(newUserStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public UserStatusResponseDTO findById(UUID targetUserStatusId) {
        UserStatusEntity userStatus = findEntityById(targetUserStatusId);

        return toResponseDTO(userStatus);
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatusResponseDTO> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 사용자 상태 수정
    @Override
    public UserStatusResponseDTO update(UUID userStatusId, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        UserStatusEntity targetUserStatus = findEntityById(userStatusId);

        Optional.ofNullable(userStatusUpdateRequestDTO.userStatusType())
                .ifPresent(userStatusType -> {
                    targetUserStatus.updateStatus(userStatusType);
                    userStatusRepository.save(targetUserStatus);
                });

        targetUserStatus.updateLastOnlineTime();
        userStatusRepository.save(targetUserStatus);

        return toResponseDTO(targetUserStatus);
    }

    // 특정 사용자의 상태 변경
    @Override
    public UserStatusResponseDTO updateByUserId(UUID targetUserStatusId) {
        UserEntity targetUser = userRepository.findById(targetUserStatusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserStatusEntity targetUserStatus = userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(targetUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));

        targetUserStatus.updateLastOnlineTime();
        userStatusRepository.save(targetUserStatus);

        return toResponseDTO(targetUserStatus);
    }

    //사용자 상태 삭제
    @Override
    public void delete(UUID id) {
        UserStatusEntity targetUserStatus = findEntityById(id);
        userStatusRepository.delete(targetUserStatus);
    }

    // 단일 엔티티 반환
    public UserStatusEntity findEntityById(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));
    }

    // 응답 DTO 생성 및 반환
    public UserStatusResponseDTO toResponseDTO(UserStatusEntity userStatus) {
        return UserStatusResponseDTO.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUserId())
                .userStatusType(userStatus.getStatus())
                .createdAt(userStatus.getCreatedAt())
                .updatedAt(userStatus.getUpdatedAt())
                .lastOnlineTime(userStatus.getLastOnlineTime())
                .build();
    }
}
