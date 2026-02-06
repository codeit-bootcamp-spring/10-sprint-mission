package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDTO;
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
    public UserStatusResponseDTO create(UserStatusCreateRequestDTO userStatusCreateRequestDTO) {
        UUID userId = userStatusCreateRequestDTO.userId();
        Instant lastAccessTime = userStatusCreateRequestDTO.lastAccessTime();
        // 관련된 User가 존재하지 않으면 예외를 발생
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userId+"를 가진 User를 찾지 못했습니다");
        }
        // 같은 User와 관련된 객체가 이미 존재하면 예외를 발생
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException(userId+"를 가진 UserState가 이미 존재합니다");
        }
        UserStatus userStatus = new UserStatus(userId, lastAccessTime);
        return toUserStatusResponseDTO(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusResponseDTO find(UUID userStatusId) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        return toUserStatusResponseDTO(userStatus);
    }

    @Override
    public List<UserStatusResponseDTO> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toUserStatusResponseDTO)
                .toList();
    }

    @Override
    public UserStatusResponseDTO update(UUID userStatusId, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        return toUserStatusResponseDTO(processUpdate(userStatus, userStatusUpdateRequestDTO));
    }

    @Override
    public UserStatusResponseDTO updateByUserId(UUID userId, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(userId+"를 가진 UserStatus를 찾지 못했습니다"));
        return toUserStatusResponseDTO(processUpdate(userStatus, userStatusUpdateRequestDTO));
    }
    // update()와 updateByUserId userStatus를 가져온 후 update하는 로직이 겹치는데 따로 메소드화?
    // 나중에 update에 대한게 추가 되거나 update할게 많아진다면 update,updateByUserId 등등을 다 바꾸지 않아도 되게끔?
    private UserStatus processUpdate(UserStatus userStatus, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        Instant lastAccessTime = userStatusUpdateRequestDTO.lastAccessTime();
        userStatus.updateLastAccessTime(lastAccessTime);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if(!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException(userStatusId+"를 가진 UserStatus를 찾지 못했습니다");
        }
        userStatusRepository.deleteById(userStatusId);
    }

    // UserStatus를 간단한 응답용 DTO로 변환하는 메서드
    private UserStatusResponseDTO toUserStatusResponseDTO(UserStatus userStatus) {
        return new UserStatusResponseDTO(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastAccessTime()
        );
    }

    // UserStatusRepository.findById()를 통한 반복되는 UserStatus 조회/예외처리를 중복제거 하기 위한 메서드
    private UserStatus getUserStatusByIdOrThrow(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("userStatusId:"+userStatusId+"를 가진 UserStatus를 찾지 못했습니다"));
    }
}
